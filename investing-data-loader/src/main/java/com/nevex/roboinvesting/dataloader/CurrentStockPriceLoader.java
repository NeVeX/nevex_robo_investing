package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.TestingControlUtil;
import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickerEntity;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class CurrentStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(CurrentStockPriceLoader.class);
    private final AtomicBoolean isUnlockedFromDataLoaders = new AtomicBoolean(false);
    private final TickersRepository tickersRepository;
    private final TiingoApiClient tiingoApiClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;

    public CurrentStockPriceLoader(TickersRepository tickersRepository,
                                   TiingoApiClient tiingoApiClient,
                                   StockPriceAdminService stockPriceAdminService,
                                   long waitTimeBetweenTickersMs) {
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( tiingoApiClient == null) { throw new IllegalArgumentException("Provided tiingoApiClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.tiingoApiClient = tiingoApiClient;
        this.stockPriceAdminService = stockPriceAdminService;
    }

    @Override
    boolean canHaveExceptions() {
        return false;
    }

    @Override
    int orderNumber() {
        return DataLoaderOrder.STOCK_PRICE_CURRENT_LOADER;
    }

    @Override
    void doWork() throws DataLoadWorkerException {
        // unlock this loader
        isUnlockedFromDataLoaders.set(true);
        LOGGER.info("Unlocked the lock so this loader can now fetch current price data on it's schedule");
    }

    // Run this Monday to Friday, at 8pm
    @Scheduled(cron = "0 0 20 * * MON-FRI", zone = "America/Los_Angeles")
//    @Scheduled(cron = "*/10 * * * * *", zone = "America/Los_Angeles") // Every 10 seconds
    void getAllCurrentPrices() {
        LOGGER.info("Current prices job has started!");

        if ( !isUnlockedFromDataLoaders.get()) {
            LOGGER.warn("Cannot continue job since the loader lock is not released yet");
        }

        // Fetch all the ticker symbols we have
        super.processAllPagesForRepo(tickersRepository, this::loadCurrentPrice, waitTimeBetweenTickersMs);

        LOGGER.info("Current prices job has finished!");
    }

    private void loadCurrentPrice(TickerEntity tickerEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickerEntity.getSymbol())) {
//            LOGGER.info("Not processing symbol [{}] since testing control does not allow it", tickerEntity.getSymbol());
            return;
        }

        try {
            Optional<TiingoPriceDto> tiingoPriceOpt = tiingoApiClient.getCurrentPriceForSymbol(tickerEntity.getSymbol());
            if ( tiingoPriceOpt.isPresent()) {
                stockPriceAdminService.saveNewCurrentPrice(tickerEntity.getSymbol(), tiingoPriceOpt.get());
            } else {
                LOGGER.info("No current price information was returned for [{}]", tickerEntity.getSymbol());
            }

        } catch (Exception e) {
            LOGGER.error("An error occured trying to get current price for symbol [{}]", tickerEntity.getSymbol(), e);
        }
    }

}

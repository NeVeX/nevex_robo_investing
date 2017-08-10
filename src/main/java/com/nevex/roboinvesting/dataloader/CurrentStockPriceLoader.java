package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import com.nevex.roboinvesting.util.TestingControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

    public CurrentStockPriceLoader(TickersRepository tickersRepository, TiingoApiClient tiingoApiClient, StockPriceAdminService stockPriceAdminService) {
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( tiingoApiClient == null) { throw new IllegalArgumentException("Provided tiingoApiClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        this.tickersRepository = tickersRepository;
        this.tiingoApiClient = tiingoApiClient;
        this.stockPriceAdminService = stockPriceAdminService;
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
        super.processAllPagesForRepo(tickersRepository, this::loadCurrentPrice, TimeUnit.MINUTES.toMillis(1));

        LOGGER.info("Current prices job has finished!");
    }

    private void loadCurrentPrice(TickersEntity tickersEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickersEntity.getSymbol())) {
//            LOGGER.info("Not processing symbol [{}] since testing control does not allow it", tickersEntity.getSymbol());
            return;
        }

        try {
            Optional<TiingoPriceDto> tiingoPriceOpt = tiingoApiClient.getCurrentPriceForSymbol(tickersEntity.getSymbol());
            if ( tiingoPriceOpt.isPresent()) {
                stockPriceAdminService.saveNewCurrentPrice(tickersEntity.getSymbol(), tiingoPriceOpt.get());
            } else {
                LOGGER.info("No current price information was returned for [{}]", tickersEntity.getSymbol());
            }

        } catch (Exception e) {
            LOGGER.error("An error occured trying to get current price for symbol [{}]", tickersEntity.getSymbol(), e);
        }
    }

}

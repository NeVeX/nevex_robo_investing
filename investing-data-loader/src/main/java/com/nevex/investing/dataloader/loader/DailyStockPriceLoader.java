package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.api.tiingo.model.TiingoPriceDto;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class DailyStockPriceLoader extends DataLoaderSchedulingSingleWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(DailyStockPriceLoader.class);
    private final AtomicBoolean isUnlockedFromDataLoaders = new AtomicBoolean(false);
    private final TickersRepository tickersRepository;
    private final TiingoApiClient tiingoApiClient;
    private final EventManager eventManager;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;

    public DailyStockPriceLoader(TickersRepository tickersRepository,
                                 TiingoApiClient tiingoApiClient,
                                 StockPriceAdminService stockPriceAdminService,
                                 DataLoaderService dataLoaderService,
                                 EventManager eventManager,
                                 long waitTimeBetweenTickersMs,
                                 boolean forceStartOnActivation) {
        super(dataLoaderService, forceStartOnActivation);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( tiingoApiClient == null) { throw new IllegalArgumentException("Provided tiingoApiClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( eventManager == null) { throw new IllegalArgumentException("Provided eventManager is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.tiingoApiClient = tiingoApiClient;
        this.stockPriceAdminService = stockPriceAdminService;
        this.eventManager = eventManager;
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.STOCK_PRICE_CURRENT_LOADER;
    }

    @Override
    public String getName() {
        return "current-stock-price-loader";
    }

     // Run this Monday to Friday, at 8pm
    @Scheduled(cron = "0 0 20 * * MON-FRI", zone = "America/Los_Angeles")
//    @Scheduled(cron = "*/10 * * * * *", zone = "America/Los_Angeles") // Every 10 seconds
    @Override
    void onScheduleStartInvoked() {
        super.scheduleStart();
    }

    @Override
    DataLoaderWorkerResult onWorkerStartedAtAppStartup() throws DataLoaderWorkerException {
        // unlock this loader
        isUnlockedFromDataLoaders.set(true);
        LOGGER.info("[{}] - unlocked the lock so this loader can now fetch current price data on it's schedule", getName());
        return DataLoaderWorkerResult.nothingDone();
    }

    @Override
    DataLoaderWorkerResult doScheduledWork() throws DataLoaderWorkerException {
        if ( !isUnlockedFromDataLoaders.get()) {
            LOGGER.warn("[{}] - cannot perform scheduled work since the loader lock is not released yet", getName());
            return DataLoaderWorkerResult.nothingDone();
        }
        return new DataLoaderWorkerResult(getAllCurrentPrices());
    }

    private int getAllCurrentPrices() {
        // Fetch all the ticker symbols we have
        return super.processAllPagesIndividuallyForRepo(tickersRepository, this::loadCurrentPrice, waitTimeBetweenTickersMs);
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
                eventManager.addDailyStockPriceUpdateEvent(tickerEntity.getId());
            } else {
                LOGGER.info("No current price information was returned for [{}]", tickerEntity.getSymbol());
            }
        } catch (Exception e) {
            saveExceptionToDatabase("Could not save current price for ticker ["+tickerEntity.getSymbol()+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("An error occurred trying to get current price for symbol [{}]", tickerEntity.getSymbol(), e);
        }
    }

}

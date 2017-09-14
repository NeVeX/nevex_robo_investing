package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.api.tiingo.model.TiingoPriceDto;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.DailyStockPriceEventProcessor;
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
    private final ApiStockPriceClient apiStockPriceClient;
    private final DailyStockPriceEventProcessor dailyStockPriceEventProcessor;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;

    public DailyStockPriceLoader(TickersRepository tickersRepository,
                                 ApiStockPriceClient apiStockPriceClient,
                                 StockPriceAdminService stockPriceAdminService,
                                 DataLoaderService dataLoaderService,
                                 DailyStockPriceEventProcessor dailyStockPriceEventProcessor,
                                 long waitTimeBetweenTickersMs,
                                 boolean forceStartOnActivation) {
        super(dataLoaderService, forceStartOnActivation);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( apiStockPriceClient == null) { throw new IllegalArgumentException("Provided apiStockPriceClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( dailyStockPriceEventProcessor == null) { throw new IllegalArgumentException("Provided dailyStockPriceEventProcessor is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.apiStockPriceClient = apiStockPriceClient;
        this.stockPriceAdminService = stockPriceAdminService;
        this.dailyStockPriceEventProcessor = dailyStockPriceEventProcessor;
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
            Optional<ApiStockPrice> tiingoPriceOpt = apiStockPriceClient.getPriceForSymbol(tickerEntity.getSymbol());
            if ( tiingoPriceOpt.isPresent()) {
                stockPriceAdminService.saveNewCurrentPrice(tickerEntity.getSymbol(), tiingoPriceOpt.get());
                dailyStockPriceEventProcessor.addEvent(tickerEntity.getId());
            } else {
                LOGGER.info("No current price information was returned for [{}]", tickerEntity.getSymbol());
            }
        } catch (Exception e) {
            saveExceptionToDatabase("Could not save current price for ticker ["+tickerEntity.getSymbol()+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("An error occurred trying to get current price for symbol [{}]", tickerEntity.getSymbol(), e);
        }
    }

}

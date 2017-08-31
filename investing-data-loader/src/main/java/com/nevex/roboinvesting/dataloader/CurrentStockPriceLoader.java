package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.TestingControlUtil;
import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
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
    private final AtomicBoolean isWorkerRunning = new AtomicBoolean(false);
    private final TickersRepository tickersRepository;
    private final DataLoaderRunsRepository dataLoaderRunsRepository;
    private final TiingoApiClient tiingoApiClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;
    private final boolean forceStartOnActivation;

    public CurrentStockPriceLoader(TickersRepository tickersRepository,
                                   TiingoApiClient tiingoApiClient,
                                   StockPriceAdminService stockPriceAdminService,
                                   DataLoaderRunsRepository dataLoaderRunsRepository,
                                   DataLoaderErrorsRepository errorsRepository,
                                   long waitTimeBetweenTickersMs,
                                   boolean forceStartOnActivation) {
        super(errorsRepository);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( tiingoApiClient == null) { throw new IllegalArgumentException("Provided tiingoApiClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( dataLoaderRunsRepository == null) { throw new IllegalArgumentException("Provided dataLoaderRunsRepository is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.tiingoApiClient = tiingoApiClient;
        this.stockPriceAdminService = stockPriceAdminService;
        this.forceStartOnActivation = forceStartOnActivation;
        this.dataLoaderRunsRepository = dataLoaderRunsRepository; // need this since this job has it's own schedule
    }

    @Override
    boolean canHaveExceptions() {
        return false;
    }

    @Override
    int getOrderNumber() {
        return DataLoaderOrder.STOCK_PRICE_CURRENT_LOADER;
    }

    @Override
    String getName() {
        return "current-stock-price-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoadWorkerException {
        // unlock this loader
        isUnlockedFromDataLoaders.set(true);
        int totalRecordsProcessed = 0;
        LOGGER.info("Unlocked the lock so this loader can now fetch current price data on it's schedule");
        if ( forceStartOnActivation ) {
            LOGGER.info("Will start the [{}] immediately since it is told to force start", getName());
            totalRecordsProcessed = getAllCurrentPrices();
        }
        return new DataLoaderWorkerResult(totalRecordsProcessed);
    }

    // Run this Monday to Friday, at 8pm
    @Scheduled(cron = "0 0 20 * * MON-FRI", zone = "America/Los_Angeles")
//    @Scheduled(cron = "*/10 * * * * *", zone = "America/Los_Angeles") // Every 10 seconds
    int getAllCurrentPrices() {
        LOGGER.info("Current prices job has started!");

        if ( !isUnlockedFromDataLoaders.get()) {
            LOGGER.warn("Cannot continue job since the loader lock is not released yet");
            return 0;
        }

        boolean isWorkerAlreadyRunning = isWorkerRunning.getAndSet(true); // get the current value and set to true for now
        if ( isWorkerAlreadyRunning ) {
            LOGGER.warn("There is already a current stock price loader running - will not invoke another worker");
            return 0;
        }

        try {
            // Fetch all the ticker symbols we have
            return super.processAllPagesForRepo(tickersRepository, this::loadCurrentPrice, waitTimeBetweenTickersMs);
        } finally {
            LOGGER.info("Current prices job has finished!");
            isWorkerRunning.set(false); // reset it
        }
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
            saveExceptionToDatabase("Could not save current price for ticker ["+tickerEntity.getSymbol()+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("An error occurred trying to get current price for symbol [{}]", tickerEntity.getSymbol(), e);
        }
    }

}

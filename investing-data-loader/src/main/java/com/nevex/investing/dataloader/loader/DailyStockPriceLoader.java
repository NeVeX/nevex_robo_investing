package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.api.tiingo.model.TiingoPriceDto;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.DailyStockPriceEventProcessor;
import com.nevex.investing.service.ServiceException;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    private final TickerService tickerService;
    private final long waitTimeBetweenTickersMs;
    private final boolean useBulkMode = true;

    public DailyStockPriceLoader(TickersRepository tickersRepository,
                                 ApiStockPriceClient apiStockPriceClient,
                                 StockPriceAdminService stockPriceAdminService,
                                 DataLoaderService dataLoaderService,
                                 DailyStockPriceEventProcessor dailyStockPriceEventProcessor,
                                 TickerService tickerService,
                                 long waitTimeBetweenTickersMs,
                                 boolean forceStartOnActivation) {
        super(dataLoaderService, forceStartOnActivation);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( apiStockPriceClient == null) { throw new IllegalArgumentException("Provided apiStockPriceClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( dailyStockPriceEventProcessor == null) { throw new IllegalArgumentException("Provided dailyStockPriceEventProcessor is null"); }
        if ( tickerService == null) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.apiStockPriceClient = apiStockPriceClient;
        this.stockPriceAdminService = stockPriceAdminService;
        this.dailyStockPriceEventProcessor = dailyStockPriceEventProcessor;
        this.tickerService = tickerService;
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
        if ( useBulkMode ) {
            return super.processAllPagesInBulkForRepo(tickersRepository, this::loadCurrentPrices, 2000, 50);
        } else {
            return super.processAllPagesIndividuallyForRepo(tickersRepository, this::loadCurrentPrice, waitTimeBetweenTickersMs);
        }
    }

    private void loadCurrentPrices(List<TickerEntity> tickerEntities) {
        List<String> tickers = tickerEntities.stream().map(TickerEntity::getSymbol).collect(Collectors.toList());
        tickers = TestingControlUtil.getAllowedTickers(tickers);
        Map<String, Optional<ApiStockPrice>> prices = new HashMap<>();
        try {
            prices = apiStockPriceClient.getPriceForSymbols(tickers);
        } catch (ApiException apiEx) {
            saveExceptionToDatabase("Could not get current prices in bulk for ["+tickers.size()+"] tickers. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("An error occurred trying to get current prices in bulk for [{}] tickers", tickers.size(), apiEx);
        }

        if ( prices.isEmpty()) {
            LOGGER.warn("No stock prices were returned for [{}] tickers", tickers.size());
            return;
        }

        for ( Map.Entry<String, Optional<ApiStockPrice>> entry : prices.entrySet()) {
            if ( entry.getValue().isPresent()) {
                savePrice(entry.getKey(), entry.getValue().get());
            }
        }

    }

    private void loadCurrentPrice(TickerEntity tickerEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickerEntity.getSymbol())) {
            return;
        }
        Optional<ApiStockPrice> apiStockPriceOpt = Optional.empty();
        try {
            apiStockPriceOpt = apiStockPriceClient.getPriceForSymbol(tickerEntity.getSymbol());
        } catch (ApiException apiEx) {
            saveExceptionToDatabase("Could not save current price for ticker ["+tickerEntity.getSymbol()+"]. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("An error occurred trying to get current price for symbol [{}]", tickerEntity.getSymbol(), apiEx);
        }

        if ( apiStockPriceOpt.isPresent()) {
            savePrice(tickerEntity.getSymbol(), apiStockPriceOpt.get());
        } else {
            LOGGER.info("No current price information was returned for [{}]", tickerEntity.getSymbol());
        }
    }

    private void savePrice(String symbol, ApiStockPrice stockPrice) {
        try {
            stockPriceAdminService.saveNewCurrentPrice(symbol, stockPrice);
            dailyStockPriceEventProcessor.addEvent(tickerService.getIdForSymbol(symbol));
        } catch (Exception ex) {
            LOGGER.error("Could not save the stock price for ticker [{}]", symbol, ex);
        }
    }

}

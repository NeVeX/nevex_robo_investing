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
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class HistoricalStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoricalStockPriceLoader.class);
    private final static int DEFAULT_MAX_DAYS_HISTORICAL = TimePeriod.OneYear.getDays();
    private final TickersRepository tickersRepository;
    private final ApiStockPriceClient apiStockPriceClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;
    private final boolean useBulkMode = true;

    public HistoricalStockPriceLoader(TickersRepository tickersRepository,
                                      ApiStockPriceClient apiStockPriceClient,
                                      StockPriceAdminService stockPriceAdminService,
                                      DataLoaderService dataLoaderService,
                                      long waitTimeBetweenTickersMs) {
        super(dataLoaderService);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( apiStockPriceClient == null) { throw new IllegalArgumentException("Provided apiStockPriceClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( waitTimeBetweenTickersMs < 0) { throw new IllegalArgumentException("Provided waitTimeBetweenTickersMs ["+waitTimeBetweenTickersMs+"] is invalid"); }
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        this.tickersRepository = tickersRepository;
        this.apiStockPriceClient = apiStockPriceClient;
        this.stockPriceAdminService = stockPriceAdminService;
    }

    @Override
    public String getName() {
        return "historical-stock-price-loader";
    }

    @Override
    @Transactional
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        LOGGER.info("{} will start to do it's work", this.getClass());

        // Fetch all the ticker symbols we have
        int totalRecordsProcessed = 0;
        if ( useBulkMode) {
            super.processAllPagesInBulkForRepo(tickersRepository, this::loadHistoricalPricesForSymbols, 3000, 20);
        } else {
            super.processAllPagesIndividuallyForRepo(tickersRepository, this::loadHistoricalPricesForSymbol, waitTimeBetweenTickersMs);
        }

        LOGGER.info("{} has completed all it's work", this.getClass());
        return new DataLoaderWorkerResult(totalRecordsProcessed);
    }

    private void loadHistoricalPricesForSymbols(List<TickerEntity> tickerEntities) {
        List<String> tickers = tickerEntities.stream().map(TickerEntity::getSymbol).collect(Collectors.toList());
        tickers = TestingControlUtil.getAllowedTickers(tickers); // remove tickers that are not allowed under test
        Map<String, Set<ApiStockPrice>> prices = null;
        try {
            prices = apiStockPriceClient.getHistoricalPricesForSymbols(tickers, DEFAULT_MAX_DAYS_HISTORICAL);
        } catch (ApiException apiEx) {
            saveExceptionToDatabase("Could not get historical price for bulk ["+tickers.size()+"] symbols. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("Could not bulk get historical prices for symbols [{}]", tickers, apiEx);
        }
        if ( prices == null || prices.isEmpty()) {
            LOGGER.warn("Received no bulk prices for a total of [{}] tickers", tickers.size());
            return;
        }

        for ( Map.Entry<String, Set<ApiStockPrice>> entry : prices.entrySet()) {
            savePrices(entry.getKey(), entry.getValue());
        }

        LOGGER.info("Successfully bulk loaded [{}] symbol's worth of historical prices", tickers.size());
    }

    private void loadHistoricalPricesForSymbol(TickerEntity tickerEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickerEntity.getSymbol())) {
            return;
        }

        Set<ApiStockPrice> historicalPrices = null;
        try {
            historicalPrices = apiStockPriceClient.getHistoricalPricesForSymbol(tickerEntity.getSymbol(), DEFAULT_MAX_DAYS_HISTORICAL);
        } catch (ApiException apiEx ) {
            saveExceptionToDatabase("Could not get historical price for symbol symbol ["+tickerEntity.getSymbol()+"]. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("Could not get historical prices for symbol [{}]", tickerEntity.getSymbol(), apiEx);
        }

        if ( historicalPrices == null || historicalPrices.isEmpty()) {
            LOGGER.warn("Received no historical prices for stock [{}]", tickerEntity.getSymbol());
            return;
        }
        LOGGER.info("Successfully loaded [{}] historical prices for [{}]", historicalPrices.size(), tickerEntity.getSymbol());
    }

    private void savePrices(String symbol, Set<ApiStockPrice> prices) {
        try {
            stockPriceAdminService.saveHistoricalPrices(symbol, prices);
        } catch (Exception e) {
            saveExceptionToDatabase("Could not save historical price for symbol ["+symbol+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("Could not save historical prices for symbol [{}]", symbol, e);
        }
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.STOCK_PRICE_HISTORICAL_LOADER;
    }

}

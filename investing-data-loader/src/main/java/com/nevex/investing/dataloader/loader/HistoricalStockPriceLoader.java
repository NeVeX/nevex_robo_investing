package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.service.StockPriceAdminService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class HistoricalStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoricalStockPriceLoader.class);
    private final TickersRepository tickersRepository;
    private final ApiStockPriceClient apiStockPriceClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;
    private final boolean useBulkMode;
    private final int maxDaysToFetch;
    private final long waitTimeBetweenBulkMs;
    private final int bulkAmountPerPage;

    public HistoricalStockPriceLoader(TickersRepository tickersRepository,
                                      ApiStockPriceClient apiStockPriceClient,
                                      StockPriceAdminService stockPriceAdminService,
                                      DataLoaderService dataLoaderService,
                                      DataLoaderProperties.HistoricalStockLoaderProperties properties) {
        super(dataLoaderService);
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( apiStockPriceClient == null) { throw new IllegalArgumentException("Provided apiStockPriceClient is null"); }
        if ( stockPriceAdminService == null) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        this.waitTimeBetweenTickersMs = properties.getWaitTimeBetweenTickersMs();
        this.tickersRepository = tickersRepository;
        this.apiStockPriceClient = apiStockPriceClient;
        this.stockPriceAdminService = stockPriceAdminService;
        this.useBulkMode = properties.getUseBulkMode();
        this.maxDaysToFetch = properties.getMaxDaysToFetch();
        this.waitTimeBetweenBulkMs = properties.getWaitTimeBetweenBulkMs();
        this.bulkAmountPerPage = properties.getBulkAmountPerPage();
    }

    @Override
    public String getName() {
        return "historical-stock-price-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        // Fetch all the ticker symbols we have
        LOGGER.info("{} will fetch a maximum of [{}] historical days", getName(), maxDaysToFetch);
        int totalRecordsProcessed;
        if ( useBulkMode) {
            totalRecordsProcessed = super.processAllPagesInBulkForRepo(tickersRepository, this::loadHistoricalPricesForSymbols, waitTimeBetweenBulkMs, bulkAmountPerPage);
        } else {
            totalRecordsProcessed = super.processAllPagesIndividuallyForRepo(tickersRepository, this::loadHistoricalPricesForSymbol, waitTimeBetweenTickersMs, 10);
        }
        return new DataLoaderWorkerResult(totalRecordsProcessed);
    }

    private void loadHistoricalPricesForSymbols(List<TickerEntity> tickerEntities) {
        List<String> tickers = tickerEntities.stream().map(TickerEntity::getSymbol).collect(Collectors.toList());
        tickers = TestingControlUtil.getAllowedTickers(tickers); // remove tickers that are not allowed under test
        Map<String, Set<ApiStockPrice>> prices = null;
        try {
            prices = apiStockPriceClient.getHistoricalPricesForSymbols(tickers, getWorkerStartDate(), maxDaysToFetch);
        } catch (ApiException apiEx) {
            saveExceptionToDatabase("Could not get historical price for bulk ["+tickers.size()+"] symbols. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("Could not bulk get historical prices for symbols [{}]", tickers, apiEx);
        }
        if ( prices == null || prices.isEmpty()) {
            LOGGER.warn("Received no bulk prices for a total of [{}] tickers", tickers.size());
            return;
        }

        for ( Map.Entry<String, Set<ApiStockPrice>> entry : prices.entrySet()) {
            // TODO: Don't do this - we already have the list above, shouldn't be looping again!!!!
            Optional<TickerEntity> tickerEntityOpt = tickerEntities.stream().filter( te -> StringUtils.equalsIgnoreCase(te.getSymbol(), entry.getKey())).findFirst();
            if ( tickerEntityOpt.isPresent()) {
                savePrices(tickerEntityOpt.get(), entry.getValue());
            }
        }

        LOGGER.info("Successfully bulk loaded [{}] symbol's worth of historical prices", tickers.size());
    }

    private void loadHistoricalPricesForSymbol(TickerEntity tickerEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickerEntity.getSymbol())) {
            return;
        }

        Set<ApiStockPrice> historicalPrices = null;
        try {
            historicalPrices = apiStockPriceClient.getHistoricalPricesForSymbol(tickerEntity.getSymbol(), getWorkerStartDate(), maxDaysToFetch);
        } catch (ApiException apiEx ) {
            saveExceptionToDatabase("Could not get historical price for symbol symbol ["+tickerEntity.getSymbol()+"]. Reason: ["+apiEx.getMessage()+"]");
            LOGGER.error("Could not get historical prices for symbol [{}]", tickerEntity.getSymbol(), apiEx);
        }

        if ( historicalPrices == null || historicalPrices.isEmpty()) {
            LOGGER.warn("Received no historical prices for stock [{}]", tickerEntity.getSymbol());
            return;
        }
        if ( savePrices(tickerEntity, historicalPrices) ) {

            LOGGER.info("Successfully loaded [{}] historical prices for [{}]", historicalPrices.size(), tickerEntity.getSymbol());
        }
    }

    private boolean savePrices(TickerEntity tickerEntity, Set<ApiStockPrice> prices) {
        try {
            stockPriceAdminService.saveHistoricalPrices(tickerEntity.getSymbol(), prices);
            sendEvents(tickerEntity.getId(), prices);
            return true;
        } catch (Exception e) {
            saveExceptionToDatabase("Could not save historical price for symbol ["+tickerEntity.getSymbol()+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("Could not save historical prices for symbol [{}]", tickerEntity.getSymbol(), e);
            return false;
        }
    }

    private void sendEvents(int tickerId, Set<ApiStockPrice> prices) {
        // just send one event for now, the latest date
        prices.stream()
                .map(ApiStockPrice::getDate)
                .reduce((l1, l2) -> l1.isAfter(l2) ? l1 : l2)
                .ifPresent(date -> EventManager.sendEvent(new StockPriceUpdatedEvent(tickerId, date)));
        // TODO: Uncomment the below when the system can handle so many events (or really, when the event is purposeful)
//        Set<LocalDate> dates = prices.stream().map(ApiStockPrice::getDate).collect(Collectors.toSet());
//        dates.stream().forEach(date -> EventManager.sendEvent(new StockPriceUpdatedEvent(tickerId, date)));
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.STOCK_PRICE_HISTORICAL_LOADER;
    }

}

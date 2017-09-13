package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
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

import java.util.Set;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class HistoricalStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoricalStockPriceLoader.class);
    private final static int DEFAULT_MAX_DAYS_HISTORICAL = TimePeriod.OneYear.getDays();
    private final TickersRepository tickersRepository;
    private final TiingoApiClient tiingoApiClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;

    public HistoricalStockPriceLoader(TickersRepository tickersRepository,
                                      TiingoApiClient tiingoApiClient,
                                      StockPriceAdminService stockPriceAdminService,
                                      DataLoaderService dataLoaderService,
                                      long waitTimeBetweenTickersMs) {
        super(dataLoaderService);
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
    public String getName() {
        return "historical-stock-price-loader";
    }

    @Override
    @Transactional
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        LOGGER.info("{} will start to do it's work", this.getClass());

        // Fetch all the ticker symbols we have
        int totalRecordsProcessed = super.processAllPagesIndividuallyForRepo(tickersRepository, this::loadHistoricalPricesForSymbol, waitTimeBetweenTickersMs);

        LOGGER.info("{} has completed all it's work", this.getClass());
        return new DataLoaderWorkerResult(totalRecordsProcessed);
    }

    private void loadHistoricalPricesForSymbol(TickerEntity tickerEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickerEntity.getSymbol())) {
//            LOGGER.info("Not processing symbol [{}] since testing control does not allow it", tickerEntity.getSymbol());
            return;
        }

        Set<TiingoPriceDto> historicalPrices;
        try {
            historicalPrices = tiingoApiClient.getHistoricalPricesForSymbol(tickerEntity.getSymbol(), DEFAULT_MAX_DAYS_HISTORICAL);

            if ( historicalPrices == null || historicalPrices.isEmpty()) {
                LOGGER.warn("Received no historical prices for stock [{}]", tickerEntity.getSymbol());
                return;
            }

            stockPriceAdminService.saveHistoricalPrices(tickerEntity.getSymbol(), historicalPrices);

            LOGGER.info("Successfully loaded [{}] historical prices for [{}]", historicalPrices.size(), tickerEntity.getSymbol());

        } catch (Exception e ) {
            saveExceptionToDatabase("Could not save historical price for symbol ["+tickerEntity.getSymbol()+"]. Reason: ["+e.getMessage()+"]");
            LOGGER.error("Could not get historical prices for symbol [{}]", tickerEntity.getSymbol(), e);
        }
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.STOCK_PRICE_HISTORICAL_LOADER;
    }

}

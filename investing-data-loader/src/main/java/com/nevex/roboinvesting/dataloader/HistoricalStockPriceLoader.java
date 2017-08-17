package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.TestingControlUtil;
import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class HistoricalStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoricalStockPriceLoader.class);
    private final static int DEFAULT_MAX_DAYS_HISTORICAL = 365;
    private final TickersRepository tickersRepository;
    private final TiingoApiClient tiingoApiClient;
    private final StockPriceAdminService stockPriceAdminService;
    private final long waitTimeBetweenTickersMs;

    public HistoricalStockPriceLoader(TickersRepository tickersRepository,
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
    @Transactional
    public void doWork() throws DataLoadWorkerException {
        LOGGER.info("{} will start to do it's work", this.getClass());

        // Fetch all the ticker symbols we have
        super.processAllPagesForRepo(tickersRepository, this::loadHistoricalPricesForSymbol, waitTimeBetweenTickersMs);

        LOGGER.info("{} has completed all it's work", this.getClass());
    }

    private void loadHistoricalPricesForSymbol(TickersEntity tickersEntity) {

        if (!TestingControlUtil.isTickerAllowed(tickersEntity.getSymbol())) {
//            LOGGER.info("Not processing symbol [{}] since testing control does not allow it", tickersEntity.getSymbol());
            return;
        }

        Set<TiingoPriceDto> historicalPrices;
        try {
            historicalPrices = tiingoApiClient.getHistoricalPricesForSymbol(tickersEntity.getSymbol(), DEFAULT_MAX_DAYS_HISTORICAL);

            if ( historicalPrices == null || historicalPrices.isEmpty()) {
                LOGGER.warn("Received no historical prices for stock [{}]", tickersEntity.getSymbol());
                return;
            }

            stockPriceAdminService.saveHistoricalPrices(tickersEntity.getSymbol(), historicalPrices);

            LOGGER.info("Successfully loaded [{}] historical prices for [{}]", historicalPrices.size(), tickersEntity.getSymbol());

        } catch (Exception e ) {
            LOGGER.error("Could not get historical prices for symbol [{}]", tickersEntity.getSymbol(), e);
        }
    }


    @Override
    boolean canHaveExceptions() {
        return false;
    }

    @Override
    public int orderNumber() {
        return DataLoaderOrder.STOCK_PRICE_HISTORICAL_LOADER;
    }

}

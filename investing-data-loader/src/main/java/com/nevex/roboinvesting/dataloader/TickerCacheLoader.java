package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.TestingControlUtil;
import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickerEntity;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import com.nevex.roboinvesting.service.TickerAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class TickerCacheLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerCacheLoader.class);
    private final TickerAdminService tickerAdminService;

    public TickerCacheLoader(TickerAdminService tickerAdminService, DataLoaderErrorsRepository errorsRepository) {
        super(errorsRepository);
        if ( tickerAdminService == null ) { throw new IllegalArgumentException("ticker admin service is null"); }
        this.tickerAdminService = tickerAdminService;
    }

    @Override
    String getName() {
        return "ticker-cache-loader";
    }

    @Override
    boolean canHaveExceptions() {
        return false;
    }

    @Override
    int getOrderNumber() {
        return DataLoaderOrder.TICKER_CACHE_LOADER;
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoadWorkerException {
        // unlock this loader
        int totalTickersRefreshed = tickerAdminService.refreshAllTickers();
        LOGGER.info("Ticker cache loader job completed");
        return new DataLoaderWorkerResult(totalTickersRefreshed);
    }

}

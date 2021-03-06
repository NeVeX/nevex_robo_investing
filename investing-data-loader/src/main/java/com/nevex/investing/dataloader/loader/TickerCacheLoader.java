package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.TickerAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class TickerCacheLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerCacheLoader.class);
    private final TickerAdminService tickerAdminService;

    public TickerCacheLoader(TickerAdminService tickerAdminService, DataLoaderService dataLoaderService) {
        super(dataLoaderService);
        if ( tickerAdminService == null ) { throw new IllegalArgumentException("ticker admin service is null"); }
        this.tickerAdminService = tickerAdminService;
    }

    @Override
    public String getName() {
        return "ticker-cache-loader";
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.TICKER_CACHE_LOADER;
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        // unlock this loader
        int totalTickersRefreshed = tickerAdminService.refreshAllTickers();
        LOGGER.info("Ticker cache loader job completed");
        return new DataLoaderWorkerResult(totalTickersRefreshed);
    }

}

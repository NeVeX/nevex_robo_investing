package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_FUNDAMENTALS_SYNC_LOADER;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerFundamentalsSyncLoader extends DataLoaderWorker {

    public TickerFundamentalsSyncLoader(DataLoaderService dataLoaderService) {
        super(dataLoaderService);
    }

    @Override
    public int getOrderNumber() {
        return TICKER_FUNDAMENTALS_SYNC_LOADER;
    }

    @Override
    public String getName() {
        return "ticker-fundamentals-sync-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        return DataLoaderWorkerResult.nothingDone();
    }
}

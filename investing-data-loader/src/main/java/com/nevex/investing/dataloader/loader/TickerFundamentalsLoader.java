package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_FUNDAMENTALS_LOADER;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerFundamentalsLoader extends DataLoaderWorker {

    public TickerFundamentalsLoader(DataLoaderService dataLoaderService) {
        super(dataLoaderService);
    }

    @Override
    int getOrderNumber() {
        return TICKER_FUNDAMENTALS_LOADER;
    }

    @Override
    String getName() {
        return "ticker-fundamentals-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        return null;
    }
}

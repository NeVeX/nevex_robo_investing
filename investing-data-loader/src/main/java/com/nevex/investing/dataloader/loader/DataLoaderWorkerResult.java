package com.nevex.investing.dataloader.loader;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
class DataLoaderWorkerResult {

    private final int recordsProcessed;

    DataLoaderWorkerResult(int recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    int getRecordsProcessed() {
        return recordsProcessed;
    }

    static DataLoaderWorkerResult nothingDone() {
        return new DataLoaderWorkerResult(0);
    }

}

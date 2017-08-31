package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
public class DataLoaderWorkerResult {

    private final int recordsProcessed;

    DataLoaderWorkerResult(int recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

}

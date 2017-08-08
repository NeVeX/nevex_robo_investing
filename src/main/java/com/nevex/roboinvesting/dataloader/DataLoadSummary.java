package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class DataLoadSummary {

    private int totalRecordsAdded;

    DataLoadSummary(int totalRecordsAdded) {
        this.totalRecordsAdded = totalRecordsAdded;
    }

    public int getTotalRecordsAdded() {
        return totalRecordsAdded;
    }
}

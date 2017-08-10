package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public abstract class DataLoaderWorker implements Comparable<DataLoaderWorker> {

    abstract int orderNumber();

    abstract void doWork();

    @Override
    public final int compareTo(DataLoaderWorker that) {
        return Integer.compare(orderNumber(), that.orderNumber());
    }

}

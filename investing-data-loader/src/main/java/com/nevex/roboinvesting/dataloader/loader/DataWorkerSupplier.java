package com.nevex.roboinvesting.dataloader.loader;

/**
 * Created by Mark Cunningham on 9/2/2017.
 * <br>Functional interface for various ways to start workers
 */
@FunctionalInterface
interface DataWorkerSupplier {

    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException;

}

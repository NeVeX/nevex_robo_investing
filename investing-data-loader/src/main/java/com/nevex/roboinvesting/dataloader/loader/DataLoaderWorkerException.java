package com.nevex.roboinvesting.dataloader.loader;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
class DataLoaderWorkerException extends Exception {

    DataLoaderWorkerException(String message) {
        super(message);
    }

    DataLoaderWorkerException(String message, Throwable t) {
        super(message, t);
    }

}

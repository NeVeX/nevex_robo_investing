package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class DataLoadWorkerException extends Exception {

    DataLoadWorkerException(String message) {
        super(message);
    }

    DataLoadWorkerException(String message, Throwable t) {
        super(message, t);
    }

}

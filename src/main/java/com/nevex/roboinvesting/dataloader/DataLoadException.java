package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class DataLoadException extends Exception {

    DataLoadException(String message) {
        super(message);
    }

    DataLoadException(String message, Throwable t) {
        super(message, t);
    }

}

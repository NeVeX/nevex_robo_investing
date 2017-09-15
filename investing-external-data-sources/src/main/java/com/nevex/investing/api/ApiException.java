package com.nevex.investing.api;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

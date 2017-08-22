package com.nevex.roboinvesting.api.tiingo;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class TiingoApiException extends Exception {

    public TiingoApiException(String message) {
        super(message);
    }

    public TiingoApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

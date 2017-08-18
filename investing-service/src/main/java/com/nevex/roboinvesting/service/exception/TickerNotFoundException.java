package com.nevex.roboinvesting.service.exception;

/**
 * Created by Mark Cunningham on 8/17/2017.
 */
public final class TickerNotFoundException extends Exception {

    public TickerNotFoundException(String symbol) {
        super("The ticker for symbol ["+symbol+"] was not found");
    }

    @Override // Suppress the stack, it's not needed
    public synchronized Throwable fillInStackTrace() {
        return this;
    }


}

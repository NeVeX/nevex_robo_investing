package com.nevex.investing.service;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class ServiceException extends Exception {

    ServiceException(String message) {
        super(message);
    }

    ServiceException(String message, Exception e) {
        super(message, e);
    }

}

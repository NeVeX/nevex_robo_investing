package com.nevex.investing.service.model;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception e) {
        super(message, e);
    }

}

package com.nevex.investing.database.model;

/**
 * Created by Mark Cunningham on 9/20/2017.
 * <br>Exception type used for wrapper data exceptions (data access)
 */
public class DataSaveException extends Exception {

    private final Object dataFailedToSave;

    public DataSaveException(Object dataFailedToSave, Throwable cause) {
        super(cause);
        this.dataFailedToSave = dataFailedToSave;
    }

    public Object getDataFailedToSave() {
        return dataFailedToSave;
    }
}

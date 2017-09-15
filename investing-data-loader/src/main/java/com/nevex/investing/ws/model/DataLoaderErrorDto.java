package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.database.entity.DataLoaderErrorEntity;

import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
public class DataLoaderErrorDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("timestamp")
    private OffsetDateTime timestamp;

    public DataLoaderErrorDto() { }

    public DataLoaderErrorDto(DataLoaderErrorEntity entity) {
        this.name = entity.getName();
        this.errorMessage = entity.getErrorMessage();
        this.timestamp = entity.getTimestamp();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

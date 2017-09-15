package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class ErrorDto {

    @JsonProperty("message")
    private String errorMessage;

    public ErrorDto() {}

    public ErrorDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

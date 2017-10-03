package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mark Cunningham on 10/2/2017.
 */
public class NoDataDto {

    @JsonProperty("message")
    private String message = "No data to show";

    public NoDataDto() { }

    public String getMessage() {
        return message;
    }
}

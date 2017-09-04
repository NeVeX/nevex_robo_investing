package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.database.entity.DataLoaderRunEntity;

import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
public class DataLoaderRunDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("start_timestamp")
    private OffsetDateTime startTimestamp;
    @JsonProperty("end_timestamp")
    private OffsetDateTime endTimestamp;
    @JsonProperty("records_processed")
    private int recordsProcessed;

    public DataLoaderRunDto() { }

    public DataLoaderRunDto(String name, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, int recordsProcessed) {
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.recordsProcessed = recordsProcessed;
    }

    public DataLoaderRunDto(DataLoaderRunEntity entity) {
        this.name = entity.getName();
        this.startTimestamp = entity.getStartTimestamp();
        this.endTimestamp = entity.getEndTimestamp();
        this.recordsProcessed = entity.getRecordsProcessed();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(OffsetDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public OffsetDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(OffsetDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(int recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }
}

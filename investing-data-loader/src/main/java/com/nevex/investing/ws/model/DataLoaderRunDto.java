package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.database.entity.DataLoaderRunEntity;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;

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
    @JsonProperty("time_taken")
    private String timeTaken;

    public DataLoaderRunDto() { }

    public DataLoaderRunDto(String name, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, int recordsProcessed) {
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.recordsProcessed = recordsProcessed;
        timeTaken = StringUtils.stripStart(Duration.between(startTimestamp, endTimestamp).abs().toString(), "PT");
    }

    public DataLoaderRunDto(DataLoaderRunEntity entity) {
        this(entity.getName(), entity.getStartTimestamp(), entity.getEndTimestamp(), entity.getRecordsProcessed());
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

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}

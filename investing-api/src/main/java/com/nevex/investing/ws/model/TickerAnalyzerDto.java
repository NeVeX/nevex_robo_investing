package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.service.model.StockPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class TickerAnalyzerDto {

    private final static int SCALE = 4;

    @JsonProperty("ticker_id")
    private int tickerId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("weight")
    private BigDecimal weight;
    @JsonProperty("date")
    private LocalDate date;

    public TickerAnalyzerDto() { }

    public TickerAnalyzerDto(AnalyzerResult analyzerResult) {
        this.tickerId = analyzerResult.getTickerId();
        this.date = analyzerResult.getDate();
        this.weight = new BigDecimal(analyzerResult.getWeight()).setScale(SCALE, RoundingMode.HALF_EVEN);
        this.name =analyzerResult.getName();
    }

    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

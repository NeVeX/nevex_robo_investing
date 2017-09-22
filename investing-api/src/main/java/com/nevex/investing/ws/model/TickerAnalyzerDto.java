package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.service.model.StockPrice;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class TickerAnalyzerDto {

    @JsonProperty("ticker_id")
    private int tickerId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("weight")
    private double weight;
    @JsonProperty("date")
    private LocalDate date;

    public TickerAnalyzerDto() { }

    public TickerAnalyzerDto(AnalyzerResult analyzerResult) {
        this.tickerId = analyzerResult.getTickerId();
        this.date = analyzerResult.getDate();
        this.weight = analyzerResult.getWeight();
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

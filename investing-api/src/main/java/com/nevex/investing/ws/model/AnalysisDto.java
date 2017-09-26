package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mark Cunningham on 9/26/2017.
 */
public class AnalysisDto {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("summary")
    private TickerAnalyzerSummaryDto summary;

    public AnalysisDto() { }

    public AnalysisDto(String symbol, String name, TickerAnalyzerSummaryDto summary) {
        this.symbol = symbol;
        this.name = name;
        this.summary = summary;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TickerAnalyzerSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(TickerAnalyzerSummaryDto summary) {
        this.summary = summary;
    }
}

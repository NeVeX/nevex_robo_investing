package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerPricePerformanceSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public class AnalyzerPricePerformanceDto {

    private final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("ticker_id")
    private int tickerId;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("price_direction_as_expected")
    private boolean priceDirectionAsExpected;
    @JsonProperty("price_difference")
    private BigDecimal priceDifference;
    @JsonProperty("percent_difference")
    private BigDecimal percentDifference;

    public AnalyzerPricePerformanceDto(AnalyzerPricePerformance perf) {
        this.symbol = perf.getSymbol();
        this.tickerId = perf.getTickerId();
        this.date = perf.getDate();
        this.priceDirectionAsExpected = perf.isPriceDirectionAsExpected();
        this.priceDifference = perf.getPriceDifference().setScale(2, RoundingMode.HALF_EVEN);
        this.percentDifference = perf.getPercentDifference().multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_EVEN);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPriceDirectionAsExpected() {
        return priceDirectionAsExpected;
    }

    public void setPriceDirectionAsExpected(boolean priceDirectionAsExpected) {
        this.priceDirectionAsExpected = priceDirectionAsExpected;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public BigDecimal getPercentDifference() {
        return percentDifference;
    }

    public void setPercentDifference(BigDecimal percentDifference) {
        this.percentDifference = percentDifference;
    }
}

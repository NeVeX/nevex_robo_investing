package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class TickerAnalyzerSummaryDto {

    private final static int SCALE = 4;

    @JsonProperty("ticker_id")
    private int tickerId;
    @JsonProperty("average_weight")
    private BigDecimal averageWeight;
    @JsonProperty("adjusted_weight")
    private BigDecimal adjustedWeight;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("analyzer_count")
    private int analyzerCount;

    public TickerAnalyzerSummaryDto() { }

    public TickerAnalyzerSummaryDto(int tickerId, double averageWeight, double adjustedWeight, LocalDate date, int analyzerCount) {
        this.tickerId = tickerId;
        this.averageWeight = new BigDecimal(averageWeight).setScale(SCALE, RoundingMode.HALF_EVEN);
        this.adjustedWeight = new BigDecimal(adjustedWeight).setScale(SCALE, RoundingMode.HALF_EVEN);
        this.date = date;
        this.analyzerCount = analyzerCount;
    }
    public TickerAnalyzerSummaryDto(AnalyzerSummaryResult summaryResult) {
        this(summaryResult.getTickerId(), summaryResult.getAverageWeight(), summaryResult.getAdjustedWeight(), summaryResult.getDate(), summaryResult.getAnalyzerCount());
    }

    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    public BigDecimal getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(BigDecimal averageWeight) {
        this.averageWeight = averageWeight;
    }

    public BigDecimal getAdjustedWeight() {
        return adjustedWeight;
    }

    public void setAdjustedWeight(BigDecimal adjustedWeight) {
        this.adjustedWeight = adjustedWeight;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAnalyzerCount() {
        return analyzerCount;
    }

    public void setAnalyzerCount(int analyzerCount) {
        this.analyzerCount = analyzerCount;
    }
}

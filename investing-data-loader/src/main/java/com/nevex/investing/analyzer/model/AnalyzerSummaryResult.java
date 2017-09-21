package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public class AnalyzerSummaryResult {

    private final int tickerId;
    private final double averageWeight;
    private final double adjustedWeight;
    private final LocalDate date;
    private final int analyzerCount;

    public AnalyzerSummaryResult(int tickerId, double averageWeight, double adjustedWeight, int analyzerCount, LocalDate date) {
        if (date == null) { throw new IllegalArgumentException("Provided date is null"); }
        this.tickerId = tickerId;
        this.averageWeight = averageWeight;
        this.adjustedWeight = adjustedWeight;
        this.date = date;
        this.analyzerCount = analyzerCount;
    }

    public int getTickerId() {
        return tickerId;
    }

    public double getAverageWeight() {
        return averageWeight;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getAnalyzerCount() {
        return analyzerCount;
    }

    public double getAdjustedWeight() {
        return adjustedWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerSummaryResult that = (AnalyzerSummaryResult) o;
        return tickerId == that.tickerId &&
                Double.compare(that.averageWeight, averageWeight) == 0 &&
                Double.compare(that.adjustedWeight, adjustedWeight) == 0 &&
                analyzerCount == that.analyzerCount &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, averageWeight, adjustedWeight, date, analyzerCount);
    }

    @Override
    public String toString() {
        return "AnalyzerSummaryResult{" +
                "tickerId=" + tickerId +
                ", averageWeight=" + averageWeight +
                ", adjustedWeight=" + adjustedWeight +
                ", date=" + date +
                ", analyzerCount=" + analyzerCount +
                '}';
    }
}

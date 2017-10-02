package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.AnalyzerPricePerformanceEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public final class AnalyzerPricePerformance {

    private final String symbol;
    private final int tickerId;
    private final LocalDate date;
    private final boolean priceDirectionAsExpected;
    private final BigDecimal priceDifference;
    private final BigDecimal percentDifference;

    public AnalyzerPricePerformance(String symbol, int tickerId, LocalDate date, boolean priceDirectionAsExpected, BigDecimal priceDifference, BigDecimal percentDifference) {
        this.symbol = symbol;
        this.tickerId = tickerId;
        this.date = date;
        this.priceDirectionAsExpected = priceDirectionAsExpected;
        this.priceDifference = priceDifference;
        this.percentDifference = percentDifference;
    }

    public AnalyzerPricePerformance(String symbol, AnalyzerPricePerformanceEntity entity) {
        this.symbol = symbol;
        this.tickerId = entity.getTickerId();
        this.date = entity.getDate();
        this.priceDifference = entity.getPriceDifference();
        this.priceDirectionAsExpected = entity.getPriceDirectionAsExpected();
        this.percentDifference = entity.getPercentDifference();
    }

    @Override
    public String toString() {
        return "AnalyzerPricePerformance{" +
                "tickerId=" + tickerId +
                "symbol=" + symbol +
                ", date=" + date +
                ", priceDirectionAsExpected=" + priceDirectionAsExpected +
                ", percentDifference=" + percentDifference +
                ", priceDifference=" + priceDifference +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerPricePerformance that = (AnalyzerPricePerformance) o;
        return tickerId == that.tickerId &&
                priceDirectionAsExpected == that.priceDirectionAsExpected &&
                Objects.equals(date, that.date) &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(priceDifference, that.priceDifference) &&
                Objects.equals(percentDifference, that.percentDifference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, tickerId, date, priceDirectionAsExpected, priceDifference, percentDifference);
    }

    public String getSymbol() {
        return symbol;
    }

    public int getTickerId() {
        return tickerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isPriceDirectionAsExpected() {
        return priceDirectionAsExpected;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public BigDecimal getPercentDifference() {
        return percentDifference;
    }
}

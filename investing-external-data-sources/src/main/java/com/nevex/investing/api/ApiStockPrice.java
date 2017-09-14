package com.nevex.investing.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public final class ApiStockPrice implements Comparable<ApiStockPrice> {

    private final LocalDate date;
    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal adjustedClose;
    private final BigDecimal high;
    private final BigDecimal low;
    private final long volume;

    private ApiStockPrice(Builder builder) {
        if ( builder.date == null ) { throw new IllegalArgumentException("Date cannot be null"); }
        if ( builder.open == null ) { throw new IllegalArgumentException("Open cannot be null"); }
        if ( builder.close == null ) { throw new IllegalArgumentException("Close cannot be null"); }
        if ( builder.adjustedClose == null ) { throw new IllegalArgumentException("AdjustedClose cannot be null"); }
        if ( builder.high == null ) { throw new IllegalArgumentException("High cannot be null"); }
        if ( builder.low == null ) { throw new IllegalArgumentException("Low cannot be null"); }
        if ( builder.volume == null ) { throw new IllegalArgumentException("Volume cannot be null"); }
        this.date = builder.date;
        this.open = builder.open;
        this.close = builder.close;
        this.adjustedClose = builder.adjustedClose;
        this.high = builder.high;
        this.low = builder.low;
        this.volume = builder.volume;
    }

    public final int compareTo(ApiStockPrice other) {
        return date.compareTo(other.date) * -1; // reverse the natural order (gets latest date first)
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getAdjustedClose() {
        return adjustedClose;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public long getVolume() {
        return volume;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private LocalDate date;
        private BigDecimal open;
        private BigDecimal close;
        private BigDecimal adjustedClose;
        private BigDecimal high;
        private BigDecimal low;
        private Long volume;

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withOpen(BigDecimal open) {
            this.open = open;
            return this;
        }

        public Builder withClose(BigDecimal close) {
            this.close = close;
            return this;
        }

        public Builder withAdjustedClose(BigDecimal adjustedClose) {
            this.adjustedClose = adjustedClose;
            return this;
        }

        public Builder withHigh(BigDecimal high) {
            this.high = high;
            return this;
        }

        public Builder withLow(BigDecimal low) {
            this.low = low;
            return this;
        }

        public Builder withVolume(Long volume) {
            this.volume = volume;
            return this;
        }

        public ApiStockPrice build() throws BuilderException {
            try {
                return new ApiStockPrice(this);
            } catch (Exception e) {
                throw new BuilderException("Could not build instance of "+ApiStockPrice.class.getSimpleName()+". Reason: "+e.getMessage());
            }

        }
    }

    // For exceptions during the builder process
    public static class BuilderException extends Exception {
        private BuilderException(String message) {
            super(message);
        }
    }

}

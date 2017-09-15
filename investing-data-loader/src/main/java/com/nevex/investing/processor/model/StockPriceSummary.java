package com.nevex.investing.processor.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/13/2017.
 */
public class StockPriceSummary {

    private final BigDecimal openAvg;
    private final BigDecimal highAvg;
    private final BigDecimal lowAvg;
    private final BigDecimal closeAvg;
    private final Long volumeAvg;

    public StockPriceSummary(BigDecimal openAvg, BigDecimal highAvg, BigDecimal lowAvg, BigDecimal closeAvg, Long volumeAvg) {
        this.openAvg = openAvg;
        this.highAvg = highAvg;
        this.lowAvg = lowAvg;
        this.closeAvg = closeAvg;
        this.volumeAvg = volumeAvg;
    }

    public BigDecimal getOpenAvg() {
        return openAvg;
    }

    public BigDecimal getHighAvg() {
        return highAvg;
    }

    public BigDecimal getLowAvg() {
        return lowAvg;
    }

    public BigDecimal getCloseAvg() {
        return closeAvg;
    }

    public Long getVolumeAvg() {
        return volumeAvg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceSummary that = (StockPriceSummary) o;
        return Objects.equals(openAvg, that.openAvg) &&
                Objects.equals(highAvg, that.highAvg) &&
                Objects.equals(lowAvg, that.lowAvg) &&
                Objects.equals(closeAvg, that.closeAvg) &&
                Objects.equals(volumeAvg, that.volumeAvg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openAvg, highAvg, lowAvg, closeAvg, volumeAvg);
    }

    @Override
    public String toString() {
        return "StockPriceSummary{" +
                "openAvg=" + openAvg +
                ", highAvg=" + highAvg +
                ", lowAvg=" + lowAvg +
                ", closeAvg=" + closeAvg +
                ", volumeAvg=" + volumeAvg +
                '}';
    }
}

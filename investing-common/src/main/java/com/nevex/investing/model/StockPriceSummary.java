package com.nevex.investing.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/13/2017.
 */
public class StockPriceSummary {

    private final BigDecimal openAvg;
    private final BigDecimal openLowest;
    private final BigDecimal openHighest;
    private final BigDecimal highAvg;
    private final BigDecimal highest;
    private final BigDecimal lowAvg;
    private final BigDecimal lowest;
    private final BigDecimal closeAvg;
    private final BigDecimal closeLowest;
    private final BigDecimal closeHighest;
    private final Long volumeAvg;
    private final Long volumeHighest;
    private final Long volumeLowest;

    public StockPriceSummary(BigDecimal openAvg, BigDecimal openLowest, BigDecimal openHighest, BigDecimal highAvg, BigDecimal highest, BigDecimal lowAvg, BigDecimal lowest, BigDecimal closeAvg, BigDecimal closeLowest, BigDecimal closeHighest, Long volumeAvg, Long volumeHighest, Long volumeLowest) {
        this.openAvg = openAvg;
        this.openLowest = openLowest;
        this.openHighest = openHighest;
        this.highAvg = highAvg;
        this.highest = highest;
        this.lowAvg = lowAvg;
        this.lowest = lowest;
        this.closeAvg = closeAvg;
        this.closeLowest = closeLowest;
        this.closeHighest = closeHighest;
        this.volumeAvg = volumeAvg;
        this.volumeHighest = volumeHighest;
        this.volumeLowest = volumeLowest;
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

    public BigDecimal getOpenLowest() {
        return openLowest;
    }

    public BigDecimal getOpenHighest() {
        return openHighest;
    }

    public BigDecimal getHighest() {
        return highest;
    }

    public BigDecimal getLowest() {
        return lowest;
    }

    public BigDecimal getCloseLowest() {
        return closeLowest;
    }

    public BigDecimal getCloseHighest() {
        return closeHighest;
    }

    public Long getVolumeHighest() {
        return volumeHighest;
    }

    public Long getVolumeLowest() {
        return volumeLowest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceSummary that = (StockPriceSummary) o;
        return Objects.equals(openAvg, that.openAvg) &&
                Objects.equals(openLowest, that.openLowest) &&
                Objects.equals(openHighest, that.openHighest) &&
                Objects.equals(highAvg, that.highAvg) &&
                Objects.equals(highest, that.highest) &&
                Objects.equals(lowAvg, that.lowAvg) &&
                Objects.equals(lowest, that.lowest) &&
                Objects.equals(closeAvg, that.closeAvg) &&
                Objects.equals(closeLowest, that.closeLowest) &&
                Objects.equals(closeHighest, that.closeHighest) &&
                Objects.equals(volumeAvg, that.volumeAvg) &&
                Objects.equals(volumeHighest, that.volumeHighest) &&
                Objects.equals(volumeLowest, that.volumeLowest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openAvg, openLowest, openHighest, highAvg, highest, lowAvg, lowest, closeAvg, closeLowest, closeHighest, volumeAvg, volumeHighest, volumeLowest);
    }

    @Override
    public String toString() {
        return "StockPriceSummary{" +
                "openAvg=" + openAvg +
                ", openLowest=" + openLowest +
                ", openHighest=" + openHighest +
                ", highAvg=" + highAvg +
                ", highest=" + highest +
                ", lowAvg=" + lowAvg +
                ", lowest=" + lowest +
                ", closeAvg=" + closeAvg +
                ", closeLowest=" + closeLowest +
                ", closeHighest=" + closeHighest +
                ", volumeAvg=" + volumeAvg +
                ", volumeHighest=" + volumeHighest +
                ", volumeLowest=" + volumeLowest +
                '}';
    }
}

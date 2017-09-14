package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/9/2017.
 * <br>Base class for the current and historical entities
 */
@MappedSuperclass
public abstract class StockPriceBaseEntity {

    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "open")
    private BigDecimal open;
    @Column(name = "high")
    private BigDecimal high;
    @Column(name = "low")
    private BigDecimal low;
    @Column(name = "close")
    private BigDecimal close;
    @Column(name = "volume")
    private long volume;
    @Column(name = "adj_open")
    @Deprecated
    private BigDecimal adjOpen;
    @Column(name = "adj_high")
    @Deprecated
    private BigDecimal adjHigh;
    @Column(name = "adj_low")
    @Deprecated
    private BigDecimal adjLow;
    @Column(name = "adj_close")
    private BigDecimal adjClose;
    @Column(name = "adj_volume")
    @Deprecated
    private Long adjVolume;
    @Column(name = "dividend_cash")
    @Deprecated
    private BigDecimal dividendCash;
    @Column(name = "split_factor")
    @Deprecated
    private BigDecimal splitFactor;

    public void merge(StockPriceBaseEntity other) {
        this.open = other.open;
        this.high = other.high;
        this.low = other.low;
        this.close = other.close;
        this.volume = other.volume;
        this.adjOpen = other.adjOpen;
        this.adjHigh = other.adjHigh;
        this.adjLow = other.adjLow;
        this.adjVolume = other.adjVolume;
        this.dividendCash = other.dividendCash;
        this.splitFactor = other.splitFactor;
    }

    @Override
    public String toString() {
        return "StockPriceBaseEntity{" +
                "tickerId=" + tickerId +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", adjOpen=" + adjOpen +
                ", adjHigh=" + adjHigh +
                ", adjLow=" + adjLow +
                ", adjClose=" + adjClose +
                ", adjVolume=" + adjVolume +
                ", dividendCash=" + dividendCash +
                ", splitFactor=" + splitFactor +
                '}';
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

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

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    @Deprecated
    public BigDecimal getAdjOpen() {
        return adjOpen;
    }

    @Deprecated
    public void setAdjOpen(BigDecimal adjOpen) {
        this.adjOpen = adjOpen;
    }

    @Deprecated
    public BigDecimal getAdjHigh() {
        return adjHigh;
    }

    @Deprecated
    public void setAdjHigh(BigDecimal adjHigh) {
        this.adjHigh = adjHigh;
    }

    @Deprecated
    public BigDecimal getAdjLow() {
        return adjLow;
    }

    @Deprecated
    public void setAdjLow(BigDecimal adjLow) {
        this.adjLow = adjLow;
    }

    public BigDecimal getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(BigDecimal adjClose) {
        this.adjClose = adjClose;
    }

    @Deprecated
    public Long getAdjVolume() {
        return adjVolume;
    }

    @Deprecated
    public void setAdjVolume(Long adjVolume) {
        this.adjVolume = adjVolume;
    }

    @Deprecated
    public BigDecimal getDividendCash() {
        return dividendCash;
    }

    @Deprecated
    public void setDividendCash(BigDecimal dividendCash) {
        this.dividendCash = dividendCash;
    }

    @Deprecated
    public BigDecimal getSplitFactor() {
        return splitFactor;
    }

    @Deprecated
    public void setSplitFactor(BigDecimal splitFactor) {
        this.splitFactor = splitFactor;
    }





}

package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices_historical",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date"}))
public class StockPriceHistoricalEntity implements StockPriceBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    @Override
    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    @Override
    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    @Override
    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    @Override
    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    @Override
    @Deprecated
    public BigDecimal getAdjOpen() {
        return adjOpen;
    }

    @Deprecated
    public void setAdjOpen(BigDecimal adjOpen) {
        this.adjOpen = adjOpen;
    }

    @Override
    @Deprecated
    public BigDecimal getAdjHigh() {
        return adjHigh;
    }

    @Deprecated
    public void setAdjHigh(BigDecimal adjHigh) {
        this.adjHigh = adjHigh;
    }

    @Override
    @Deprecated
    public BigDecimal getAdjLow() {
        return adjLow;
    }

    @Deprecated
    public void setAdjLow(BigDecimal adjLow) {
        this.adjLow = adjLow;
    }

    @Override
    public BigDecimal getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(BigDecimal adjClose) {
        this.adjClose = adjClose;
    }

    @Override
    @Deprecated
    public Long getAdjVolume() {
        return adjVolume;
    }

    @Deprecated
    public void setAdjVolume(Long adjVolume) {
        this.adjVolume = adjVolume;
    }

    @Override
    @Deprecated
    public BigDecimal getDividendCash() {
        return dividendCash;
    }

    @Deprecated
    public void setDividendCash(BigDecimal dividendCash) {
        this.dividendCash = dividendCash;
    }

    @Override
    @Deprecated
    public BigDecimal getSplitFactor() {
        return splitFactor;
    }

    @Deprecated
    public void setSplitFactor(BigDecimal splitFactor) {
        this.splitFactor = splitFactor;
    }

    @Override
    public String toString() {
        return "StockPriceHistoricalEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
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

    public void merge(StockPriceHistoricalEntity other) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceHistoricalEntity that = (StockPriceHistoricalEntity) o;
        return Objects.equals(tickerId, that.tickerId) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, date);
    }
}

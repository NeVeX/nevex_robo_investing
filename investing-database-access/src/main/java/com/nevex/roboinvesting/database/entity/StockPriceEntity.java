package com.nevex.roboinvesting.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices", uniqueConstraints = @UniqueConstraint(columnNames = "ticker_id"))
public class StockPriceEntity implements StockPriceBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
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
    private BigDecimal adjOpen;
    @Column(name = "adj_high")
    private BigDecimal adjHigh;
    @Column(name = "adj_low")
    private BigDecimal adjLow;
    @Column(name = "adj_close")
    private BigDecimal adjClose;
    @Column(name = "adj_volume")
    private Long adjVolume;
    @Column(name = "dividend_cash")
    private BigDecimal dividendCash;
    @Column(name = "split_factor")
    private BigDecimal splitFactor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public BigDecimal getAdjOpen() {
        return adjOpen;
    }

    public void setAdjOpen(BigDecimal adjOpen) {
        this.adjOpen = adjOpen;
    }

    @Override
    public BigDecimal getAdjHigh() {
        return adjHigh;
    }

    public void setAdjHigh(BigDecimal adjHigh) {
        this.adjHigh = adjHigh;
    }

    @Override
    public BigDecimal getAdjLow() {
        return adjLow;
    }

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
    public Long getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(Long adjVolume) {
        this.adjVolume = adjVolume;
    }

    @Override
    public BigDecimal getDividendCash() {
        return dividendCash;
    }

    public void setDividendCash(BigDecimal dividendCash) {
        this.dividendCash = dividendCash;
    }

    @Override
    public BigDecimal getSplitFactor() {
        return splitFactor;
    }

    public void setSplitFactor(BigDecimal splitFactor) {
        this.splitFactor = splitFactor;
    }

    @Override
    public String toString() {
        return "StockPriceEntity{" +
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

    public void merge(StockPriceEntity other) {
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
        StockPriceEntity that = (StockPriceEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

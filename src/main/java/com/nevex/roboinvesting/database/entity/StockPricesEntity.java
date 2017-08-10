package com.nevex.roboinvesting.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices")
public class StockPricesEntity implements StockPriceBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "symbol")
    private String symbol;
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
    private int volume;

    @Column(name = "adj_open")
    private BigDecimal adjOpen;
    @Column(name = "adj_high")
    private BigDecimal adjHigh;
    @Column(name = "adj_low")
    private BigDecimal adjLow;
    @Column(name = "adj_close")
    private BigDecimal adjClose;
    @Column(name = "adj_volume")
    private Integer adjVolume;
    @Column(name = "dividend_cash")
    private BigDecimal dividendCash;
    @Column(name = "split_factor")
    private BigDecimal splitFactor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
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
    public Integer getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(Integer adjVolume) {
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
        return "StockPriceEntityBase{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
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
}

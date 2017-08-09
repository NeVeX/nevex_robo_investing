package com.nevex.roboinvesting.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices_historical")
public class StockPricesHistoricalEntity {

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
    private int adjVolume;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public BigDecimal getAdjOpen() {
        return adjOpen;
    }

    public void setAdjOpen(BigDecimal adjOpen) {
        this.adjOpen = adjOpen;
    }

    public BigDecimal getAdjHigh() {
        return adjHigh;
    }

    public void setAdjHigh(BigDecimal adjHigh) {
        this.adjHigh = adjHigh;
    }

    public BigDecimal getAdjLow() {
        return adjLow;
    }

    public void setAdjLow(BigDecimal adjLow) {
        this.adjLow = adjLow;
    }

    public BigDecimal getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(BigDecimal adjClose) {
        this.adjClose = adjClose;
    }

    public int getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(int adjVolume) {
        this.adjVolume = adjVolume;
    }

    public BigDecimal getDividendCash() {
        return dividendCash;
    }

    public void setDividendCash(BigDecimal dividendCash) {
        this.dividendCash = dividendCash;
    }

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

package com.nevex.roboinvesting.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.service.model.StockPrice;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class StockPriceDto {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("open")
    private BigDecimal open;
    @JsonProperty("high")
    private BigDecimal high;
    @JsonProperty("low")
    private BigDecimal low;
    @JsonProperty("close")
    private BigDecimal close;
    @JsonProperty("volume")
    private int volume;
    @JsonProperty("adj_open")
    private BigDecimal adjOpen;
    @JsonProperty("adj_high")
    private BigDecimal adjHigh;
    @JsonProperty("adj_low")
    private BigDecimal adjLow;
    @JsonProperty("adj_close")
    private BigDecimal adjClose;
    @JsonProperty("adj_volume")
    private Integer adjVolume;
    @JsonProperty("dividend_cash")
    private BigDecimal dividendCash;
    @JsonProperty("split_factor")
    private BigDecimal splitFactor;

    public StockPriceDto() { }

    public StockPriceDto(StockPrice stockPrice) {
        this.symbol = stockPrice.getSymbol();
        this.date = stockPrice.getDate();
        this.open = stockPrice.getOpen();
        this.high = stockPrice.getHigh();
        this.low = stockPrice.getLow();
        this.close = stockPrice.getClose();
        this.volume = stockPrice.getVolume();
        this.adjOpen = stockPrice.getAdjOpen().isPresent() ? stockPrice.getAdjOpen().get() : null;
        this.adjHigh = stockPrice.getAdjHigh().isPresent() ? stockPrice.getAdjHigh().get() : null;
        this.adjLow = stockPrice.getAdjLow().isPresent() ? stockPrice.getAdjLow().get() : null;
        this.adjClose = stockPrice.getAdjClose().isPresent() ? stockPrice.getAdjClose().get() : null;
        this.adjVolume = stockPrice.getAdjVolume().isPresent() ? stockPrice.getAdjVolume().get() : null;
        this.dividendCash = stockPrice.getDividendCash().isPresent() ? stockPrice.getDividendCash().get() : null;
        this.splitFactor = stockPrice.getSplitFactor().isPresent() ? stockPrice.getSplitFactor().get() : null;
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

    public Integer getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(Integer adjVolume) {
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
}

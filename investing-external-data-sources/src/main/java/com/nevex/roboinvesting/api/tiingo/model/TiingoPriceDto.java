package com.nevex.roboinvesting.api.tiingo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.api.ApiStockPrice;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TiingoPriceDto extends ApiStockPrice {

    @JsonProperty("date")
    private OffsetDateTime date;
    @JsonProperty("open")
    private BigDecimal open;
    @JsonProperty("high")
    private BigDecimal high;
    @JsonProperty("low")
    private BigDecimal low;
    @JsonProperty("close")
    private BigDecimal close;
    @JsonProperty("volume")
    private Long volume;
    @JsonProperty("adjOpen")
    private BigDecimal adjOpen;
    @JsonProperty("adjHigh")
    private BigDecimal adjHigh;
    @JsonProperty("adjLow")
    private BigDecimal adjLow;
    @JsonProperty("adjClose")
    private BigDecimal adjClose;
    @JsonProperty("adjVolume")
    private Long adjVolume;
    @JsonProperty("divCash")
    private BigDecimal divCash;
    @JsonProperty("splitFactor")
    private BigDecimal splitFactor;

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
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

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
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

    public Long getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(Long adjVolume) {
        this.adjVolume = adjVolume;
    }

    public BigDecimal getDivCash() {
        return divCash;
    }

    public void setDivCash(BigDecimal divCash) {
        this.divCash = divCash;
    }

    public BigDecimal getSplitFactor() {
        return splitFactor;
    }

    public void setSplitFactor(BigDecimal splitFactor) {
        this.splitFactor = splitFactor;
    }


}

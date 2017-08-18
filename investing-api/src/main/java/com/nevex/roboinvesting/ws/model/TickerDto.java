package com.nevex.roboinvesting.ws.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.service.model.Ticker;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TickerDto {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sector")
    private String sector;
    @JsonProperty("industry")
    private String industry;
    @JsonProperty("is_tradable")
    private boolean isTradable;
    @JsonProperty("stock_exchange")
    private String stockExchange;

    public TickerDto() { }

    public TickerDto(Ticker ticker) {
        this.name = ticker.getName();
        this.symbol = ticker.getSymbol();
        this.sector = ticker.getSector();
        this.industry = ticker.getIndustry();
        this.isTradable = ticker.isTradable();
        this.stockExchange = ticker.getStockExchange().name().toUpperCase();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public boolean getIsTradable() {
        return isTradable;
    }

    public void setIsTradable(boolean isTradable) {
        this.isTradable = isTradable;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }
}

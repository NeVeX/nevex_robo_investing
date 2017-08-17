package com.nevex.roboinvesting.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.service.model.TickerSymbol;

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

    public static TickerDto from(TickerSymbol tickerSymbol) {
        TickerDto dto = new TickerDto();
        dto.name = tickerSymbol.getName();
        dto.symbol = tickerSymbol.getSymbol();
        dto.sector = tickerSymbol.getSector();
        dto.industry = tickerSymbol.getIndustry();
        dto.isTradable = tickerSymbol.isTradable();
        dto.stockExchange = tickerSymbol.getStockExchange().name().toUpperCase();
        return dto;
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

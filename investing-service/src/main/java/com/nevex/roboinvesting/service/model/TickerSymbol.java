package com.nevex.roboinvesting.service.model;

import com.nevex.roboinvesting.database.entity.TickersEntity;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public final class TickerSymbol {

    private final String symbol;
    private final String name;
    private final String sector;
    private final String industry;
    private final boolean isTradable;
    private final StockExchange stockExchange;

    public TickerSymbol(TickersEntity tickersEntity, StockExchange stockExchange) {
        this.symbol = tickersEntity.getSymbol();
        this.name = tickersEntity.getName();
        this.sector = tickersEntity.getSector();
        this.industry = tickersEntity.getIndustry();
        this.isTradable = tickersEntity.getIsTradable();
        this.stockExchange = stockExchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }

    public StockExchange getStockExchange() {
        return stockExchange;
    }

    public boolean isTradable() {
        return isTradable;
    }
}

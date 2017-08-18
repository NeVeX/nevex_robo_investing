package com.nevex.roboinvesting.service.model;

import com.nevex.roboinvesting.database.entity.TickersEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public final class Ticker {

    private final String symbol;
    private final String name;
    private final String sector;
    private final String industry;
    private final boolean isTradable;
    private final StockExchange stockExchange;

    public Ticker(TickersEntity tickersEntity, StockExchange stockExchange) {
        if ( tickersEntity == null ) { throw new IllegalArgumentException("TickersEntity cannot be null"); }
        if ( stockExchange == null ) { throw new IllegalArgumentException("StockExchange cannot be null"); }

        this.symbol = tickersEntity.getSymbol();
        this.name = tickersEntity.getName();
        this.sector = tickersEntity.getSector();
        this.industry = tickersEntity.getIndustry();
        this.isTradable = tickersEntity.getIsTradable();
        this.stockExchange = stockExchange;

        if (StringUtils.isBlank(this.symbol) ) { throw new IllegalArgumentException("Symbol cannot be blank"); }
        if (StringUtils.isBlank(this.name) ) { throw new IllegalArgumentException("Name cannot be blank"); }
        if (StringUtils.isBlank(this.sector) ) { throw new IllegalArgumentException("Sector cannot be blank"); }
        if (StringUtils.isBlank(this.industry) ) { throw new IllegalArgumentException("Industry cannot be blank"); }
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

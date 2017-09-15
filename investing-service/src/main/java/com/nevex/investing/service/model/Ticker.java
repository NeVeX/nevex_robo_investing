package com.nevex.investing.service.model;

import com.nevex.investing.database.entity.TickerEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    public Ticker(TickerEntity tickerEntity, StockExchange stockExchange) {
        if ( tickerEntity == null ) { throw new IllegalArgumentException("TickerEntity cannot be null"); }
        if ( stockExchange == null ) { throw new IllegalArgumentException("StockExchange cannot be null"); }

        this.symbol = tickerEntity.getSymbol();
        this.name = tickerEntity.getName();
        this.sector = tickerEntity.getSector();
        this.industry = tickerEntity.getIndustry();
        this.isTradable = tickerEntity.getIsTradable();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker = (Ticker) o;
        return Objects.equals(symbol, ticker.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", industry='" + industry + '\'' +
                ", isTradable=" + isTradable +
                ", stockExchange=" + stockExchange +
                '}';
    }
}

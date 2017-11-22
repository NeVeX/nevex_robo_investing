package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "tickers", uniqueConstraints = @UniqueConstraint(columnNames = "symbol"))
public class TickerEntity implements MergeableEntity<TickerEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "symbol")
    private String symbol;
    @Column(name = "name")
    private String name;
    @Column(name = "sector")
    private String sector;
    @Column(name = "industry")
    private String industry;
    @Column(name = "created_date", columnDefinition = "timestamptz")
    private OffsetDateTime createdDate;
    @Column(name = "stock_exchange")
    private short stockExchange;
    @Column(name = "is_tradable")
    private boolean isTradable;
    @Column(name = "trading_end_date", columnDefinition = "DATE")
    private LocalDate tradingEndDate;

    @Override
    public void merge(TickerEntity other) {
        this.symbol = other.symbol;
        this.name = other.name;
        this.sector = other.sector;
        this.industry = other.industry;
        this.createdDate = other.createdDate;
        this.stockExchange = other.stockExchange;
        this.isTradable = other.isTradable;
        this.tradingEndDate = other.tradingEndDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public short getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(short stockExchange) {
        this.stockExchange = stockExchange;
    }

    public boolean getIsTradable() {
        return isTradable;
    }

    public void setIsTradable(boolean isTradable) {
        this.isTradable = isTradable;
    }

    public LocalDate getTradingEndDate() {
        return tradingEndDate;
    }

    public void setTradingEndDate(LocalDate tradingEndDate) {
        this.tradingEndDate = tradingEndDate;
    }

    @Override
    public String toString() {
        return "TickerEntity{" +
            "id=" + id +
            ", symbol='" + symbol + '\'' +
            ", name='" + name + '\'' +
            ", sector='" + sector + '\'' +
            ", industry='" + industry + '\'' +
            ", createdDate=" + createdDate +
            ", stockExchange=" + stockExchange +
            ", isTradable=" + isTradable +
            ", tradingEndDate=" + tradingEndDate +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerEntity that = (TickerEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}

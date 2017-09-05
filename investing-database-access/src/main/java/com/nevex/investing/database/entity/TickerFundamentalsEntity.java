package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Entity
@Table(schema = "investing", name = "ticker_fundamentals",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "period_end", "period_type"}))
public class TickerFundamentalsEntity implements Comparable<TickerFundamentalsEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "period_end", columnDefinition = "DATE")
    private LocalDate periodEnd;
    @Column(name = "period_type")
    private char periodType;
    @Column(name = "earnings_per_share")
    private BigDecimal earningsPerShare;
    @Column(name = "common_shares_outstanding")
    private Long commonSharesOutstanding;
    @Column(name = "stock_holders_equity")
    private Long stockHoldersEquity;

    public TickerFundamentalsEntity() {  }

    public TickerFundamentalsEntity(int tickerId, LocalDate periodEnd, char periodType, BigDecimal earningsPerShare, Long commonSharesOutstanding, Long stockHoldersEquity) {
        this.tickerId = tickerId;
        this.periodEnd = periodEnd;
        this.periodType = periodType;
        this.earningsPerShare = earningsPerShare;
        this.commonSharesOutstanding = commonSharesOutstanding;
        this.stockHoldersEquity = stockHoldersEquity;
    }

    public void merge(TickerFundamentalsEntity newData) {
        this.earningsPerShare = newData.earningsPerShare;
        this.commonSharesOutstanding = newData.commonSharesOutstanding;
        this.stockHoldersEquity = newData.stockHoldersEquity;
    }

    @Override
    public int compareTo(TickerFundamentalsEntity that) {
        return this.periodEnd.compareTo(that.periodEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerFundamentalsEntity that = (TickerFundamentalsEntity) o;
        return tickerId == that.tickerId &&
                periodType == that.periodType &&
                Objects.equals(periodEnd, that.periodEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, periodEnd, periodType);
    }

    @Override
    public String toString() {
        return "TickerFundamentalsEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", periodEnd=" + periodEnd +
                ", periodType=" + periodType +
                ", earningsPerShare=" + earningsPerShare +
                ", commonSharesOutstanding=" + commonSharesOutstanding +
                ", stockHoldersEquity=" + stockHoldersEquity +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public char getPeriodType() {
        return periodType;
    }

    public void setPeriodType(char periodType) {
        this.periodType = periodType;
    }

    public BigDecimal getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(BigDecimal earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public Long getCommonSharesOutstanding() {
        return commonSharesOutstanding;
    }

    public void setCommonSharesOutstanding(Long commonSharesOutstanding) {
        this.commonSharesOutstanding = commonSharesOutstanding;
    }

    public Long getStockHoldersEquity() {
        return stockHoldersEquity;
    }

    public void setStockHoldersEquity(Long stockHoldersEquity) {
        this.stockHoldersEquity = stockHoldersEquity;
    }

}

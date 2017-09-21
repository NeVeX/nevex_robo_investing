package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Entity
@Table(schema = "investing", name = "yahoo_stock_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date"}))
public class YahooStockInfoEntity implements MergeableEntity<YahooStockInfoEntity> {

    public static final String DATE_COL = "date";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = DATE_COL, columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "currency")
    private String currency;
    @Column(name = "stock_exchange")
    private String stockExchange;
    @Column(name = "ask")
    private BigDecimal ask;
    @Column(name = "ask_size")
    private Long askSize;
    @Column(name = "bid")
    private BigDecimal bid;
    @Column(name = "bid_size")
    private Long bidSize;
    @Column(name = "market_cap")
    private BigDecimal marketCap;
    @Column(name = "shares_float")
    private Long sharesFloat;
    @Column(name = "shares_outstanding")
    private Long sharesOutstanding;
    @Column(name = "shares_owned")
    private Long sharesOwned;
    @Column(name = "earnings_per_share")
    private BigDecimal earningsPerShare;
    @Column(name = "price_to_earnings_ratio")
    private BigDecimal priceToEarningsRatio;
    @Column(name = "price_earnings_to_growth_ratio")
    private BigDecimal priceEarningsToGrowthRatio;
    @Column(name = "eps_estimate_current_year")
    private BigDecimal epsEstimateCurrentYear;
    @Column(name = "eps_estimate_next_quarter")
    private BigDecimal epsEstimateNextQuarter;
    @Column(name = "eps_estimate_next_year")
    private BigDecimal epsEstimateNextYear;
    @Column(name = "price_to_book_ratio")
    private BigDecimal priceToBookRatio;
    @Column(name = "price_to_sales_ratio")
    private BigDecimal priceToSalesRatio;
    @Column(name = "book_value_per_share_ratio")
    private BigDecimal bookValuePerShareRatio;
    @Column(name = "revenue")
    private BigDecimal revenue; // ttm
    @Column(name = "ebitda")
    private BigDecimal ebitda; // ttm
    @Column(name = "one_year_target_price")
    private BigDecimal oneYearTargetPrice;
    @Column(name = "return_on_equity")
    private BigDecimal returnOnEquity;
    @Column(name = "short_ratio")
    private BigDecimal shortRatio;
    @Column(name = "annual_dividend_yield")
    private BigDecimal annualDividendYield;
    @Column(name = "annual_dividend_yield_percent")
    private BigDecimal annualDividendYieldPercent;

    public YahooStockInfoEntity() { }

    public YahooStockInfoEntity(int tickerId, LocalDate date, String currency, String stockExchange, BigDecimal ask, Long askSize, BigDecimal bid, Long bidSize, BigDecimal marketCap, Long sharesFloat, Long sharesOutstanding, Long sharesOwned, BigDecimal earningsPerShare, BigDecimal priceToEarningsRatio, BigDecimal priceEarningsToGrowthRatio, BigDecimal epsEstimateCurrentYear, BigDecimal epsEstimateNextQuarter, BigDecimal epsEstimateNextYear, BigDecimal priceToBookRatio, BigDecimal priceToSalesRatio, BigDecimal bookValuePerShareRatio, BigDecimal revenue, BigDecimal ebitda, BigDecimal oneYearTargetPrice, BigDecimal returnOnEquity, BigDecimal shortRatio, BigDecimal annualDividendYield, BigDecimal annualDividendYieldPercent) {
        this.tickerId = tickerId;
        this.date = date;
        this.currency = currency;
        this.stockExchange = stockExchange;
        this.ask = ask;
        this.askSize = askSize;
        this.bid = bid;
        this.bidSize = bidSize;
        this.marketCap = marketCap;
        this.sharesFloat = sharesFloat;
        this.sharesOutstanding = sharesOutstanding;
        this.sharesOwned = sharesOwned;
        this.earningsPerShare = earningsPerShare;
        this.priceToEarningsRatio = priceToEarningsRatio;
        this.priceEarningsToGrowthRatio = priceEarningsToGrowthRatio;
        this.epsEstimateCurrentYear = epsEstimateCurrentYear;
        this.epsEstimateNextQuarter = epsEstimateNextQuarter;
        this.epsEstimateNextYear = epsEstimateNextYear;
        this.priceToBookRatio = priceToBookRatio;
        this.priceToSalesRatio = priceToSalesRatio;
        this.bookValuePerShareRatio = bookValuePerShareRatio;
        this.revenue = revenue;
        this.ebitda = ebitda;
        this.oneYearTargetPrice = oneYearTargetPrice;
        this.returnOnEquity = returnOnEquity;
        this.shortRatio = shortRatio;
        this.annualDividendYield = annualDividendYield;
        this.annualDividendYieldPercent = annualDividendYieldPercent;
    }

    @Override
    public void merge(YahooStockInfoEntity other) {
        this.currency = other.currency;
        this.stockExchange = other.stockExchange;
        this.ask = other.ask;
        this.askSize = other.askSize;
        this.bid = other.bid;
        this.bidSize = other.bidSize;
        this.marketCap = other.marketCap;
        this.sharesFloat = other.sharesFloat;
        this.sharesOutstanding = other.sharesOutstanding;
        this.sharesOwned = other.sharesOwned;
        this.earningsPerShare = other.earningsPerShare;
        this.priceToEarningsRatio = other.priceToEarningsRatio;
        this.priceEarningsToGrowthRatio = other.priceEarningsToGrowthRatio;
        this.epsEstimateCurrentYear = other.epsEstimateCurrentYear;
        this.epsEstimateNextQuarter = other.epsEstimateNextQuarter;
        this.epsEstimateNextYear = other.epsEstimateNextYear;
        this.priceToBookRatio = other.priceToBookRatio;
        this.priceToSalesRatio = other.priceToSalesRatio;
        this.bookValuePerShareRatio = other.bookValuePerShareRatio;
        this.revenue = other.revenue;
        this.ebitda = other.ebitda;
        this.oneYearTargetPrice = other.oneYearTargetPrice;
        this.returnOnEquity = other.returnOnEquity;
        this.shortRatio = other.shortRatio;
        this.annualDividendYield = other.annualDividendYield;
        this.annualDividendYieldPercent = other.annualDividendYieldPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YahooStockInfoEntity that = (YahooStockInfoEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, date);
    }

    @Override
    public String toString() {
        return "YahooStockInfoEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", date=" + date +
                ", currency='" + currency + '\'' +
                ", stockExchange='" + stockExchange + '\'' +
                ", ask=" + ask +
                ", askSize=" + askSize +
                ", bid=" + bid +
                ", bidSize=" + bidSize +
                ", marketCap=" + marketCap +
                ", sharesFloat=" + sharesFloat +
                ", sharesOutstanding=" + sharesOutstanding +
                ", sharesOwned=" + sharesOwned +
                ", earningsPerShare=" + earningsPerShare +
                ", priceToEarningsRatio=" + priceToEarningsRatio +
                ", priceEarningsToGrowthRatio=" + priceEarningsToGrowthRatio +
                ", epsEstimateCurrentYear=" + epsEstimateCurrentYear +
                ", epsEstimateNextQuarter=" + epsEstimateNextQuarter +
                ", epsEstimateNextYear=" + epsEstimateNextYear +
                ", priceToBookRatio=" + priceToBookRatio +
                ", priceToSalesRatio=" + priceToSalesRatio +
                ", bookValuePerShareRatio=" + bookValuePerShareRatio +
                ", revenue=" + revenue +
                ", ebitda=" + ebitda +
                ", oneYearTargetPrice=" + oneYearTargetPrice +
                ", returnOnEquity=" + returnOnEquity +
                ", shortRatio=" + shortRatio +
                ", annualDividendYield=" + annualDividendYield +
                ", annualDividendYieldPercent=" + annualDividendYieldPercent +
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public Long getAskSize() {
        return askSize;
    }

    public void setAskSize(Long askSize) {
        this.askSize = askSize;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public Long getBidSize() {
        return bidSize;
    }

    public void setBidSize(Long bidSize) {
        this.bidSize = bidSize;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public Long getSharesFloat() {
        return sharesFloat;
    }

    public void setSharesFloat(Long sharesFloat) {
        this.sharesFloat = sharesFloat;
    }

    public Long getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(Long sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public Long getSharesOwned() {
        return sharesOwned;
    }

    public void setSharesOwned(Long sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    public BigDecimal getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(BigDecimal earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public BigDecimal getPriceToEarningsRatio() {
        return priceToEarningsRatio;
    }

    public void setPriceToEarningsRatio(BigDecimal priceToEarningsRatio) {
        this.priceToEarningsRatio = priceToEarningsRatio;
    }

    public BigDecimal getPriceEarningsToGrowthRatio() {
        return priceEarningsToGrowthRatio;
    }

    public void setPriceEarningsToGrowthRatio(BigDecimal priceEarningsToGrowthRatio) {
        this.priceEarningsToGrowthRatio = priceEarningsToGrowthRatio;
    }

    public BigDecimal getEpsEstimateCurrentYear() {
        return epsEstimateCurrentYear;
    }

    public void setEpsEstimateCurrentYear(BigDecimal epsEstimateCurrentYear) {
        this.epsEstimateCurrentYear = epsEstimateCurrentYear;
    }

    public BigDecimal getEpsEstimateNextQuarter() {
        return epsEstimateNextQuarter;
    }

    public void setEpsEstimateNextQuarter(BigDecimal epsEstimateNextQuarter) {
        this.epsEstimateNextQuarter = epsEstimateNextQuarter;
    }

    public BigDecimal getEpsEstimateNextYear() {
        return epsEstimateNextYear;
    }

    public void setEpsEstimateNextYear(BigDecimal epsEstimateNextYear) {
        this.epsEstimateNextYear = epsEstimateNextYear;
    }

    public BigDecimal getPriceToBookRatio() {
        return priceToBookRatio;
    }

    public void setPriceToBookRatio(BigDecimal priceToBookRatio) {
        this.priceToBookRatio = priceToBookRatio;
    }

    public BigDecimal getPriceToSalesRatio() {
        return priceToSalesRatio;
    }

    public void setPriceToSalesRatio(BigDecimal priceToSalesRatio) {
        this.priceToSalesRatio = priceToSalesRatio;
    }

    public BigDecimal getBookValuePerShareRatio() {
        return bookValuePerShareRatio;
    }

    public void setBookValuePerShareRatio(BigDecimal bookValuePerShareRatio) {
        this.bookValuePerShareRatio = bookValuePerShareRatio;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getEbitda() {
        return ebitda;
    }

    public void setEbitda(BigDecimal ebitda) {
        this.ebitda = ebitda;
    }

    public BigDecimal getOneYearTargetPrice() {
        return oneYearTargetPrice;
    }

    public void setOneYearTargetPrice(BigDecimal oneYearTargetPrice) {
        this.oneYearTargetPrice = oneYearTargetPrice;
    }

    public BigDecimal getReturnOnEquity() {
        return returnOnEquity;
    }

    public void setReturnOnEquity(BigDecimal returnOnEquity) {
        this.returnOnEquity = returnOnEquity;
    }

    public BigDecimal getShortRatio() {
        return shortRatio;
    }

    public void setShortRatio(BigDecimal shortRatio) {
        this.shortRatio = shortRatio;
    }

    public BigDecimal getAnnualDividendYield() {
        return annualDividendYield;
    }

    public void setAnnualDividendYield(BigDecimal annualDividendYield) {
        this.annualDividendYield = annualDividendYield;
    }

    public BigDecimal getAnnualDividendYieldPercent() {
        return annualDividendYieldPercent;
    }

    public void setAnnualDividendYieldPercent(BigDecimal annualDividendYieldPercent) {
        this.annualDividendYieldPercent = annualDividendYieldPercent;
    }

}

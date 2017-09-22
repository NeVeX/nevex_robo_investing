package com.nevex.investing.api.yahoo.model;

import org.apache.commons.lang3.StringUtils;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooStockInfo {

    private final String symbol;
    private final String currency;
    private final String stockExchange;

    private final BigDecimal ask;
    private final Long askSize;
    private final BigDecimal bid;
    private final Long bidSize;

    private final BigDecimal marketCap;
    private final Long sharesFloat;
    private final Long sharesOutstanding;
    private final Long sharesOwned;

    private final BigDecimal earningsPerShare;
    private final BigDecimal priceToEarningsRatio;
    private final BigDecimal priceEarningsToGrowthRatio;

    private final BigDecimal epsEstimateCurrentYear;
    private final BigDecimal epsEstimateNextQuarter;
    private final BigDecimal epsEstimateNextYear;

    private final BigDecimal priceToBookRatio;
    private final BigDecimal priceToSalesRatio;
    private final BigDecimal bookValuePerShareRatio;

    private final BigDecimal revenue; // ttm
    private final BigDecimal ebitda; // ttm
    private final BigDecimal oneYearTargetPrice;
    private final BigDecimal returnOnEquity;

    private final BigDecimal shortRatio;

    private final BigDecimal annualDividendYield;
    private final BigDecimal annualDividendYieldPercent;

    public YahooStockInfo(Stock stock) {

        if (StringUtils.isBlank(stock.getSymbol())) { throw new IllegalArgumentException("Symbol cannot be blank"); }
        this.symbol = stock.getSymbol();
        this.currency = stock.getCurrency();
        this.stockExchange = stock.getStockExchange();

        StockQuote quote = stock.getQuote();
        if ( quote != null ) {
            this.ask = quote.getAsk();
            this.askSize = quote.getAskSize();
            this.bid = quote.getBid();
            this.bidSize = quote.getBidSize();
        } else {
            this.ask = this.bid = null;
            this.askSize = this.bidSize = null;
        }

        StockStats stats = stock.getStats();
        if ( stats != null ) {
            this.marketCap = stats.getMarketCap();
            this.sharesFloat = stats.getSharesFloat();
            this.sharesOutstanding = stats.getSharesOutstanding();
            this.sharesOwned = stats.getSharesOwned();
            this.earningsPerShare = stats.getEps();
            this.priceToEarningsRatio = stats.getPe();
            this.priceEarningsToGrowthRatio = stats.getPeg();
            this.epsEstimateCurrentYear = stats.getEpsEstimateCurrentYear();
            this.epsEstimateNextQuarter = stats.getEpsEstimateNextQuarter();
            this.epsEstimateNextYear = stats.getEpsEstimateNextYear();
            this.priceToBookRatio = stats.getPriceBook();
            this.priceToSalesRatio = stats.getPriceSales();
            this.bookValuePerShareRatio = stats.getBookValuePerShare();
            this.revenue = stats.getRevenue();
            this.ebitda = stats.getEBITDA();
            this.oneYearTargetPrice = stats.getOneYearTargetPrice();
            this.returnOnEquity = stats.getROE();
            this.shortRatio = stats.getShortRatio();

        } else {
            this.sharesFloat = this.sharesOutstanding = this.sharesOwned = null;
            this.marketCap = this.earningsPerShare = this.priceToEarningsRatio = this.priceEarningsToGrowthRatio = null;
            this.epsEstimateCurrentYear = this.epsEstimateNextYear = this.epsEstimateNextQuarter = null;
            this.priceToSalesRatio = this.priceToBookRatio = this.bookValuePerShareRatio = null;
            this.revenue = this.ebitda = this.oneYearTargetPrice = this.returnOnEquity = null;
            this.shortRatio = null;
        }

        StockDividend dividend = stock.getDividend();
        if ( dividend != null ) {
            this.annualDividendYield = dividend.getAnnualYield();
            this.annualDividendYieldPercent = dividend.getAnnualYieldPercent();
        } else {
            this.annualDividendYield = this.annualDividendYieldPercent = null;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public Optional<String> getCurrency() {
        return Optional.ofNullable(currency);
    }

    public Optional<String> getStockExchange() {
        return Optional.ofNullable(stockExchange);
    }

    public Optional<BigDecimal> getAsk() {
        return Optional.ofNullable(ask);
    }

    public Optional<Long> getAskSize() {
        return Optional.ofNullable(askSize);
    }

    public Optional<BigDecimal> getBid() {
        return Optional.ofNullable(bid);
    }

    public Optional<Long> getBidSize() {
        return Optional.ofNullable(bidSize);
    }

    public Optional<BigDecimal> getMarketCap() {
        return Optional.ofNullable(marketCap);
    }

    public Optional<Long> getSharesFloat() {
        return Optional.ofNullable(sharesFloat);
    }

    public Optional<Long> getSharesOutstanding() {
        return Optional.ofNullable(sharesOutstanding);
    }

    public Optional<Long> getSharesOwned() {
        return Optional.ofNullable(sharesOwned);
    }

    public Optional<BigDecimal> getEarningsPerShare() {
        return Optional.ofNullable(earningsPerShare);
    }

    public Optional<BigDecimal> getPriceToEarningsRatio() {
        return Optional.ofNullable(priceToEarningsRatio);
    }

    public Optional<BigDecimal> getPriceEarningsToGrowthRatio() {
        return Optional.ofNullable(priceEarningsToGrowthRatio);
    }

    public Optional<BigDecimal> getEpsEstimateCurrentYear() {
        return Optional.ofNullable(epsEstimateCurrentYear);
    }

    public Optional<BigDecimal> getEpsEstimateNextQuarter() {
        return Optional.ofNullable(epsEstimateNextQuarter);
    }

    public Optional<BigDecimal> getEpsEstimateNextYear() {
        return Optional.ofNullable(epsEstimateNextYear);
    }

    public Optional<BigDecimal> getPriceToBookRatio() {
        return Optional.ofNullable(priceToBookRatio);
    }

    public Optional<BigDecimal> getPriceToSalesRatio() {
        return Optional.ofNullable(priceToSalesRatio);
    }

    public Optional<BigDecimal> getBookValuePerShareRatio() {
        return Optional.ofNullable(bookValuePerShareRatio);
    }

    public Optional<BigDecimal> getRevenue() {
        return Optional.ofNullable(revenue);
    }

    public Optional<BigDecimal> getEbitda() {
        return Optional.ofNullable(ebitda);
    }

    public Optional<BigDecimal> getOneYearTargetPrice() {
        return Optional.ofNullable(oneYearTargetPrice);
    }

    public Optional<BigDecimal> getReturnOnEquity() {
        return Optional.ofNullable(returnOnEquity);
    }

    public Optional<BigDecimal> getShortRatio() {
        return Optional.ofNullable(shortRatio);
    }

    public Optional<BigDecimal> getAnnualDividendYield() {
        return Optional.ofNullable(annualDividendYield);
    }

    public Optional<BigDecimal> getAnnualDividendYieldPercent() {
        return Optional.ofNullable(annualDividendYieldPercent);
    }
}

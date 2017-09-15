package com.nevex.investing.api.usfundamentals.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalIndicatorDto implements Comparable<UsFundamentalIndicatorDto> {

    private final LocalDate endPeriod;
    private BigDecimal earningsPerShareBasic;
    private Long commonStockSharesOutstanding;
    private Long stockHoldersEquity;
    private Long assets;
    private Long liabilities;
    private Long cashAndCashEquivalentsAtCarryingValue;

    public UsFundamentalIndicatorDto(LocalDate endPeriod) {
        this.endPeriod = endPeriod;
    }

    public Long getAssets() {
        return assets;
    }

    public void setAssets(Long assets) {
        this.assets = assets;
    }

    public Long getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(Long liabilities) {
        this.liabilities = liabilities;
    }

    public Long getCashAndCashEquivalentsAtCarryingValue() {
        return cashAndCashEquivalentsAtCarryingValue;
    }

    public void setCashAndCashEquivalentsAtCarryingValue(Long cashAndCashEquivalentsAtCarryingValue) {
        this.cashAndCashEquivalentsAtCarryingValue = cashAndCashEquivalentsAtCarryingValue;
    }

    public BigDecimal getEarningsPerShareBasic() {
        return earningsPerShareBasic;
    }

    public void setEarningsPerShareBasic(BigDecimal earningsPerShareBasic) {
        this.earningsPerShareBasic = earningsPerShareBasic;
    }

    public LocalDate getEndPeriod() {
        return endPeriod;
    }

    public Long getCommonStockSharesOutstanding() {
        return commonStockSharesOutstanding;
    }

    public void setCommonStockSharesOutstanding(Long commonStockSharesOutstanding) {
        this.commonStockSharesOutstanding = commonStockSharesOutstanding;
    }

    public Long getStockHoldersEquity() {
        return stockHoldersEquity;
    }

    public void setStockHoldersEquity(Long stockHoldersEquity) {
        this.stockHoldersEquity = stockHoldersEquity;
    }

    @Override
    public int compareTo(UsFundamentalIndicatorDto that) {
        return this.endPeriod.compareTo(that.endPeriod);
//        return -result; // reverse the order
    }

}

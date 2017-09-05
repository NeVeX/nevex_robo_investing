package com.nevex.investing.api.usfundamentals.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalIndicator implements Comparable<UsFundamentalIndicator> {

    private final LocalDate endPeriod;
    private BigDecimal earningsPerShareBasic;
    private Long commonStockSharesOutstanding;
    private Long stockHoldersEquity;

    public UsFundamentalIndicator(LocalDate endPeriod) {
        this.endPeriod = endPeriod;
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
    public int compareTo(UsFundamentalIndicator that) {
        return this.endPeriod.compareTo(that.endPeriod);
//        return -result; // reverse the order
    }

}

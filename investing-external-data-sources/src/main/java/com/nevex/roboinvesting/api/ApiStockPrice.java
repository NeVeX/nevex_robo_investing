package com.nevex.roboinvesting.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public abstract class ApiStockPrice implements Comparable<ApiStockPrice> {

    public abstract OffsetDateTime getDate();

    public abstract  BigDecimal getOpen();

    public abstract  BigDecimal getHigh();

    public abstract  BigDecimal getLow();

    public abstract  BigDecimal getClose();

    public abstract  Integer getVolume();

    public abstract  BigDecimal getAdjOpen();

    public abstract  BigDecimal getAdjHigh();

    public abstract  BigDecimal getAdjLow();

    public abstract  BigDecimal getAdjClose();

    public abstract  Integer getAdjVolume();

    public abstract  BigDecimal getDivCash();

    public abstract BigDecimal getSplitFactor();

    public int compareTo(ApiStockPrice other) {
        return getDate().compareTo(other.getDate()) * -1; // reverse the natural order (gets latest date first)
    }



}

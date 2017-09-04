package com.nevex.investing.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public interface StockPriceBaseEntity {

    int getTickerId();

    LocalDate getDate();

    BigDecimal getOpen();

    BigDecimal getHigh();

    BigDecimal getLow();

    BigDecimal getClose();

    long getVolume();

    BigDecimal getAdjOpen();

    BigDecimal getAdjHigh();

    BigDecimal getAdjLow();

    BigDecimal getAdjClose();

    Long getAdjVolume();

    BigDecimal getDividendCash();

    BigDecimal getSplitFactor();

}

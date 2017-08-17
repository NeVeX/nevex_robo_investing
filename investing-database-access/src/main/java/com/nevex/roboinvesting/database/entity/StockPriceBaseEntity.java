package com.nevex.roboinvesting.database.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public interface StockPriceBaseEntity {

    String getSymbol();

    LocalDate getDate();

    BigDecimal getOpen();

    BigDecimal getHigh();

    BigDecimal getLow();

    BigDecimal getClose();

    int getVolume();

    BigDecimal getAdjOpen();

    BigDecimal getAdjHigh();

    BigDecimal getAdjLow();

    BigDecimal getAdjClose();

    Integer getAdjVolume();

    BigDecimal getDividendCash();

    BigDecimal getSplitFactor();

}

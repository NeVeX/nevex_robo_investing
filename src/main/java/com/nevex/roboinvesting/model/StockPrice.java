package com.nevex.roboinvesting.model;

import com.nevex.roboinvesting.database.entity.StockPriceBaseEntity;
import com.nevex.roboinvesting.database.entity.StockPricesEntity;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public final class StockPrice {

    private final String symbol;
    private final LocalDate date;
    private final BigDecimal open;
    private final BigDecimal high;
    private final BigDecimal low;
    private final BigDecimal close;
    private final int volume;
    // Optionals below
    private final BigDecimal adjOpen;
    private final BigDecimal adjHigh;
    private final BigDecimal adjLow;
    private final BigDecimal adjClose;
    private final Integer adjVolume;
    private final BigDecimal dividendCash;
    private final BigDecimal splitFactor;

    public StockPrice(String symbol, LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, int volume, BigDecimal adjOpen, BigDecimal adjHigh, BigDecimal adjLow, BigDecimal adjClose, Integer adjVolume, BigDecimal dividendCash, BigDecimal splitFactor) {
        if (StringUtils.isBlank(symbol)) { throw new IllegalArgumentException("Provided symbol is blank"); }
        if (date == null) { throw new IllegalArgumentException("Provided date is null"); }
        if (open == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (high == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (low == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (close == null) { throw new IllegalArgumentException("Provided open is null"); }

        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;

        // The below can be null
        this.adjOpen = adjOpen;
        this.adjHigh = adjHigh;
        this.adjLow = adjLow;
        this.adjClose = adjClose;
        this.adjVolume = adjVolume;
        this.dividendCash = dividendCash;
        this.splitFactor = splitFactor;
    }

    public StockPrice(StockPriceBaseEntity entity) {
        this(
            entity.getSymbol(),
            entity.getDate(),
            entity.getOpen(),
            entity.getHigh(),
            entity.getLow(),
            entity.getClose(),
            entity.getVolume(),
            entity.getAdjOpen(),
            entity.getAdjHigh(),
            entity.getAdjLow(),
            entity.getAdjClose(),
            entity.getAdjVolume(),
            entity.getDividendCash(),
            entity.getSplitFactor()
        );
    }


    public String getSymbol() {
        return symbol;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    public Optional<BigDecimal> getAdjOpen() {
        return Optional.ofNullable(adjOpen);
    }

    public Optional<BigDecimal> getAdjHigh() {
        return Optional.ofNullable(adjHigh);
    }

    public Optional<BigDecimal> getAdjLow() {
        return Optional.ofNullable(adjLow);
    }

    public Optional<BigDecimal> getAdjClose() {
        return Optional.ofNullable(adjClose);
    }

    public Optional<Integer> getAdjVolume() {
        return Optional.ofNullable(adjVolume);
    }

    public Optional<BigDecimal> getDividendCash() {
        return Optional.ofNullable(dividendCash);
    }

    public Optional<BigDecimal> getSplitFactor() {
        return Optional.ofNullable(splitFactor);
    }
}

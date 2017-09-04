package com.nevex.investing.service.model;

import com.nevex.investing.database.entity.StockPriceBaseEntity;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public final class StockPrice {

    private final String tickerSymbol;
    private final LocalDate date;
    private final BigDecimal open;
    private final BigDecimal high;
    private final BigDecimal low;
    private final BigDecimal close;
    private final long volume;
    // Optionals below
    private final BigDecimal adjOpen;
    private final BigDecimal adjHigh;
    private final BigDecimal adjLow;
    private final BigDecimal adjClose;
    private final Long adjVolume;
    private final BigDecimal dividendCash;
    private final BigDecimal splitFactor;

    public StockPrice(String symbol, LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, long volume, BigDecimal adjOpen, BigDecimal adjHigh, BigDecimal adjLow, BigDecimal adjClose, Long adjVolume, BigDecimal dividendCash, BigDecimal splitFactor) {
        if (StringUtils.isBlank(symbol)) { throw new IllegalArgumentException("Provided symbol is null"); }
        if (date == null) { throw new IllegalArgumentException("Provided date is null"); }
        if (open == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (high == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (low == null) { throw new IllegalArgumentException("Provided open is null"); }
        if (close == null) { throw new IllegalArgumentException("Provided open is null"); }
        this.tickerSymbol = symbol;
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

    public StockPrice(String symbol, StockPriceBaseEntity entity) {
        this(
            symbol,
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

    public String getTickerSymbol() {
        return tickerSymbol;
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

    public long getVolume() {
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

    public Optional<Long> getAdjVolume() {
        return Optional.ofNullable(adjVolume);
    }

    public Optional<BigDecimal> getDividendCash() {
        return Optional.ofNullable(dividendCash);
    }

    public Optional<BigDecimal> getSplitFactor() {
        return Optional.ofNullable(splitFactor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPrice that = (StockPrice) o;
        return Objects.equals(tickerSymbol, that.tickerSymbol) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerSymbol, date);
    }

    @Override
    public String toString() {
        return "StockPrice{" +
                "tickerSymbol='" + tickerSymbol + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", adjOpen=" + adjOpen +
                ", adjHigh=" + adjHigh +
                ", adjLow=" + adjLow +
                ", adjClose=" + adjClose +
                ", adjVolume=" + adjVolume +
                ", dividendCash=" + dividendCash +
                ", splitFactor=" + splitFactor +
                '}';
    }
}

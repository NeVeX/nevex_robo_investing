package com.nevex.investing.analyzer.model;

import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.service.model.StockPrice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Mark Cunningham on 9/12/2017.
 */
public class StockPriceSummaryCollector implements Collector<StockPrice, StockPriceSummaryCollector, StockPriceSummary> {

    private final LocalDate asOfDate;

    public StockPriceSummaryCollector(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    private int openCounter = 0;
    private int highCounter = 0;
    private int lowCounter = 0;
    private int closeCounter = 0;
    private int volumeCounter = 0;

    private BigDecimal open = BigDecimal.ZERO;
    private BigDecimal openHighest = null;
    private BigDecimal openLowest = null;
    private BigDecimal high = BigDecimal.ZERO;
    private BigDecimal highest = null;
    private BigDecimal low = BigDecimal.ZERO;
    private BigDecimal lowest = null;
    private BigDecimal close = BigDecimal.ZERO;
    private BigDecimal closeHighest = null;
    private BigDecimal closeLowest = null;
    private BigInteger volume = BigInteger.ZERO;
    private Long volumeHighest = null;
    private Long volumeLowest = null;

    private boolean isFirstLessThanSecond(BigDecimal current, BigDecimal comparison) {
        if ( current == null ) { return true; }
        return current.compareTo(comparison) <= -1;
    }

    private boolean isFirstGreaterThanSecond(BigDecimal current, BigDecimal comparison) {
        if ( current == null ) { return true; }
        return current.compareTo(comparison) >= 1;
    }

    private boolean isFirstLessThanSecond(Long current, Long comparison) {
        if ( current == null ) { return true; }
        return current.compareTo(comparison) <= -1;
    }

    private boolean isFirstGreaterThanSecond(Long current, Long comparison) {
        if ( current == null ) { return true; }
        return current.compareTo(comparison) >= 1;
    }

    // Add the current results to the collector for each stock price we come across
    private void add(StockPriceSummaryCollector collector, StockPrice stockPrice) {
        if ( stockPrice.getOpen() != null ) {
            collector.openCounter++;
            collector.open = collector.open.add(stockPrice.getOpen());
            if ( isFirstLessThanSecond(collector.openHighest, stockPrice.getOpen())) { collector.openHighest = stockPrice.getOpen(); }
            if ( isFirstGreaterThanSecond(collector.openLowest, stockPrice.getOpen())) { collector.openLowest = stockPrice.getOpen(); }
        }
        if ( stockPrice.getHigh() != null ) {
            collector.highCounter++;
            collector.high = collector.high.add(stockPrice.getHigh());
            if ( isFirstLessThanSecond(collector.highest, stockPrice.getHigh())) { collector.highest = stockPrice.getHigh(); }
        }
        if ( stockPrice.getLow() != null ) {
            collector.lowCounter++;
            collector.low = collector.low.add(stockPrice.getLow());
            if ( isFirstGreaterThanSecond(collector.lowest, stockPrice.getLow())) { collector.lowest = stockPrice.getLow(); }
        }
        if ( stockPrice.getClose() != null ) {
            collector.closeCounter++;
            collector.close = collector.close.add(stockPrice.getClose());
            if ( isFirstLessThanSecond(collector.closeHighest, stockPrice.getClose())) { collector.closeHighest = stockPrice.getClose(); }
            if ( isFirstGreaterThanSecond(collector.closeLowest, stockPrice.getClose())) { collector.closeLowest = stockPrice.getClose(); }
        }
        collector.volumeCounter++;
        collector.volume = collector.volume.add(BigInteger.valueOf(stockPrice.getVolume()));
        if ( isFirstLessThanSecond(collector.volumeHighest, stockPrice.getVolume())) { collector.volumeHighest = stockPrice.getVolume(); }
        if ( isFirstGreaterThanSecond(collector.volumeLowest, stockPrice.getVolume())) { collector.volumeLowest = stockPrice.getVolume(); }

    }

    // We just add both collectors and return one (so it can be combined with all other results in flight)
    private StockPriceSummaryCollector combine(StockPriceSummaryCollector finalCollector, StockPriceSummaryCollector otherCollector) {
        finalCollector.openCounter += otherCollector.openCounter;
        finalCollector.highCounter += otherCollector.highCounter;
        finalCollector.lowCounter += otherCollector.lowCounter;
        finalCollector.closeCounter += otherCollector.closeCounter;
        finalCollector.volumeCounter += otherCollector.volumeCounter;

        finalCollector.open = finalCollector.open.add(finalCollector.open);
        finalCollector.high = finalCollector.high.add(finalCollector.high);
        finalCollector.low = finalCollector.low.add(finalCollector.low);
        finalCollector.close = finalCollector.close.add(finalCollector.close);
        finalCollector.volume = finalCollector.volume.add(finalCollector.volume);

        if ( isFirstGreaterThanSecond(finalCollector.lowest, otherCollector.lowest) ) { finalCollector.lowest = otherCollector.lowest; }
        if ( isFirstLessThanSecond(finalCollector.highest, otherCollector.highest) ) { finalCollector.highest = otherCollector.highest; }

        if ( isFirstGreaterThanSecond(finalCollector.openLowest, otherCollector.openLowest) ) { finalCollector.openLowest = otherCollector.openLowest; }
        if ( isFirstLessThanSecond(finalCollector.openHighest, otherCollector.openHighest) ) { finalCollector.openHighest = otherCollector.openHighest; }

        if ( isFirstGreaterThanSecond(finalCollector.closeLowest, otherCollector.closeLowest) ) { finalCollector.closeLowest = otherCollector.closeLowest; }
        if ( isFirstLessThanSecond(finalCollector.closeHighest, otherCollector.closeHighest) ) { finalCollector.closeHighest = otherCollector.closeHighest; }

        if ( isFirstGreaterThanSecond(finalCollector.volumeLowest, otherCollector.volumeLowest) ) { finalCollector.volumeLowest = otherCollector.volumeLowest; }
        if ( isFirstLessThanSecond(finalCollector.volumeHighest, otherCollector.volumeHighest) ) { finalCollector.volumeHighest = otherCollector.volumeHighest; }

        return finalCollector; // it's the combination of both one + two
    }

    // This method is called when we can calculate all results
    private StockPriceSummary getResults(StockPriceSummaryCollector collector) {

        BigDecimal openAvg = collector.openCounter > 0 ? collector.open.divide(BigDecimal.valueOf(collector.openCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal highAvg = collector.highCounter > 0 ? collector.high.divide(BigDecimal.valueOf(collector.highCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal lowAvg = collector.lowCounter > 0 ? collector.low.divide(BigDecimal.valueOf(collector.lowCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal closeAvg = collector.closeCounter > 0 ? collector.close.divide(BigDecimal.valueOf(collector.closeCounter), RoundingMode.HALF_EVEN) : null;
        Long volumeAvg = collector.volumeCounter > 0 ? collector.volume.divide(BigInteger.valueOf(collector.volumeCounter)).longValue() : null;

        return new StockPriceSummary(
                collector.asOfDate,
                openAvg, collector.openLowest, collector.openHighest,
                highAvg, collector.highest,
                lowAvg, collector.lowest,
                closeAvg, collector.closeLowest, collector.closeHighest,
                volumeAvg, collector.volumeHighest, collector.volumeLowest);
    }

    @Override
    public Supplier<StockPriceSummaryCollector> supplier() {
        return () -> new StockPriceSummaryCollector(asOfDate);
    }

    @Override
    public BiConsumer<StockPriceSummaryCollector, StockPrice> accumulator() {
        return this::add;
    }

    @Override
    public BinaryOperator<StockPriceSummaryCollector> combiner() {
        return this::combine;
    }

    @Override
    public Function<StockPriceSummaryCollector, StockPriceSummary> finisher() {
        return this::getResults;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

}

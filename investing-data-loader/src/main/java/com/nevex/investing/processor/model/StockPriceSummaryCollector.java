package com.nevex.investing.processor.model;

import com.nevex.investing.service.model.StockPrice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

    private int openCounter = 0;
    private int highCounter = 0;
    private int lowCounter = 0;
    private int closeCounter = 0;
    private int volumeCounter = 0;

    private BigDecimal open = BigDecimal.ZERO;
    private BigDecimal high = BigDecimal.ZERO;
    private BigDecimal low = BigDecimal.ZERO;
    private BigDecimal close = BigDecimal.ZERO;
    private BigInteger volume = BigInteger.ZERO;

    // Add the current results to the collector for each stock price we come across
    private void add(StockPriceSummaryCollector collector, StockPrice stockPrice) {
        if ( stockPrice.getOpen() != null ) { collector.openCounter++; collector.open = collector.open.add(stockPrice.getOpen()); }
        if ( stockPrice.getHigh() != null ) { collector.highCounter++; collector.high = collector.high.add(stockPrice.getHigh()); }
        if ( stockPrice.getLow() != null ) { collector.lowCounter++; collector.low = collector.low.add(stockPrice.getLow()); }
        if ( stockPrice.getClose() != null ) { collector.closeCounter++; collector.close = collector.close.add(stockPrice.getClose()); }

        collector.volumeCounter++;
        collector.volume = collector.volume.add(BigInteger.valueOf(stockPrice.getVolume()));
    }

    // We just add both collectors and return one (so it can be combined with all other results in flight)
    private StockPriceSummaryCollector combine(StockPriceSummaryCollector collectorOne, StockPriceSummaryCollector collectorTwo) {
        collectorOne.openCounter += collectorTwo.openCounter;
        collectorOne.highCounter += collectorTwo.highCounter;
        collectorOne.lowCounter += collectorTwo.lowCounter;
        collectorOne.closeCounter += collectorTwo.closeCounter;
        collectorOne.volumeCounter += collectorTwo.volumeCounter;

        collectorOne.open = collectorOne.open.add(collectorOne.open);
        collectorOne.high = collectorOne.high.add(collectorOne.high);
        collectorOne.low = collectorOne.low.add(collectorOne.low);
        collectorOne.close = collectorOne.close.add(collectorOne.close);
        collectorOne.volume = collectorOne.volume.add(collectorOne.volume);

        return collectorOne; // it's the combination of both one + tow
    }

    // This method is called when we can calculate all results
    private StockPriceSummary getResults(StockPriceSummaryCollector collector) {

        BigDecimal openAvg = collector.openCounter > 0 ? collector.open.divide(BigDecimal.valueOf(collector.openCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal highAvg = collector.highCounter > 0 ? collector.high.divide(BigDecimal.valueOf(collector.highCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal lowAvg = collector.lowCounter > 0 ? collector.low.divide(BigDecimal.valueOf(collector.lowCounter), RoundingMode.HALF_EVEN) : null;
        BigDecimal closeAvg = collector.closeCounter > 0 ? collector.close.divide(BigDecimal.valueOf(collector.closeCounter), RoundingMode.HALF_EVEN) : null;
        Long volumeAvg = collector.volumeCounter > 0 ? collector.volume.divide(BigInteger.valueOf(collector.volumeCounter)).longValue() : null;

        return new StockPriceSummary(openAvg, highAvg, lowAvg, closeAvg, volumeAvg);
    }

    @Override
    public Supplier<StockPriceSummaryCollector> supplier() {
        return StockPriceSummaryCollector::new;
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

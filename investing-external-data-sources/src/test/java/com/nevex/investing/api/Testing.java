package com.nevex.investing.api;

import org.junit.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public class Testing {

    @Test
    public void calcAverages() {

        StockPrice one = new StockPrice(10, 20);
        StockPrice two = new StockPrice(50, 50);

        Set<StockPrice> stocks = new HashSet<>();
        stocks.add(one);
        stocks.add(two);

        Calculator calc = new Calculator();
        Optional<StockPriceAvg> averageOpt = stocks.stream()
                .map( s -> new StockPriceAvg(s.low, s.high))
                .reduce(calc::avg);

        if ( !averageOpt.isPresent()) { return; }

        StockPriceAvg stockPriceAvg = averageOpt.get();

        StockPrice actualAvgs = calc.avg(stockPriceAvg);

        actualAvgs.toString();

    }

    private static class Calculator {

        private int lowCount = 1;
        private int highCount = 1;

        StockPriceAvg avg(StockPriceAvg one, StockPriceAvg two) {
            lowCount++;
            highCount++;
            BigInteger newLow = one.low.add(two.low);
            BigInteger newHigh = one.high.add(two.high);
            return new StockPriceAvg(newLow, newHigh);
        }

        StockPrice avg(StockPriceAvg s) {
            int lowAvg = lowCount > 0 ? s.low.divide(BigInteger.valueOf(lowCount)).intValue() : 0;
            int highAvg = highCount > 0 ? s.high.divide(BigInteger.valueOf(highCount)).intValue() : 0;
            return new StockPrice(lowAvg, highAvg);
        }
    }


    private static class StockPrice {
        private int low;
        private int high;

        public StockPrice(int low, int high) {
            this.low = low;
            this.high = high;
        }
    }

    private static class StockPriceAvg {
        private BigInteger low;
        private BigInteger high;

        StockPriceAvg(BigInteger low, BigInteger high) {
            this.low = low;
            this.high = high;
        }

        StockPriceAvg(int low, int high) {
            this.low = BigInteger.valueOf(low);
            this.high = BigInteger.valueOf(high);
        }

    }




}

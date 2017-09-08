package com.nevex.investing.processor.model;

import com.nevex.investing.service.model.StockPrice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public class StockPriceAverages {

    final BigDecimal open;
    final BigDecimal high;
    final BigDecimal low;
    final BigDecimal close;
    final BigInteger volume;

    private StockPriceAverages(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigInteger volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public StockPriceAverages(StockPrice stockPrice) {
        open = stockPrice.getOpen();
        high = stockPrice.getHigh();
        low = stockPrice.getLow();
        close = stockPrice.getClose();
        volume = BigInteger.valueOf(stockPrice.getVolume());
    }

    public static class Calculator {
        private AtomicInteger openCounter = new AtomicInteger(1);
        private AtomicInteger highCounter = new AtomicInteger(1);
        private AtomicInteger lowCounter = new AtomicInteger(1);
        private AtomicInteger closeCounter = new AtomicInteger(1);
        private AtomicInteger volumeCounter = new AtomicInteger(1);

        public StockPriceAverages add(StockPriceAverages one, StockPriceAverages two) {
            return new StockPriceAverages(
                add(new BigDecimalArithmetic(one.open), new BigDecimalArithmetic(two.open), openCounter),
                add(new BigDecimalArithmetic(one.high), new BigDecimalArithmetic(two.high), highCounter),
                add(new BigDecimalArithmetic(one.low), new BigDecimalArithmetic(two.low), lowCounter),
                add(new BigDecimalArithmetic(one.close), new BigDecimalArithmetic(two.close), closeCounter),
                add(new BigIntegerArithmetic(one.volume), new BigIntegerArithmetic(two.volume), volumeCounter)
            );
        }

        private <T> T add(Arithmetic<T> one, Arithmetic<T> two, AtomicInteger counter) {
            if ( one.get() == null && two.get() == null) { return null; }
            if ( one.get() == null ) { return two.get(); }
            if ( two.get() == null ) { return one.get(); }
            counter.incrementAndGet();
            return one.add(two.get());
        }

        private <T> T calcAverage(Arithmetic<T> one, AtomicInteger counter) {
            return one.get() == null ? null : one.divide(counter);
        }

        public Result calcAverages(StockPriceAverages averages) {
            return new Result(
                calcAverage(new BigDecimalArithmetic(averages.open), openCounter),
                calcAverage(new BigDecimalArithmetic(averages.high), highCounter),
                calcAverage(new BigDecimalArithmetic(averages.low), lowCounter),
                calcAverage(new BigDecimalArithmetic(averages.close), closeCounter),
                calcAverage(new BigIntegerArithmetic(averages.volume), volumeCounter)
            );
        }
    }

    public static class Result extends StockPriceAverages {

        private Result(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigInteger volume) {
            super(open, high, low, close, volume);
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

        public BigInteger getVolume() {
            return volume;
        }
    }

    private static abstract class Arithmetic<T> {

        final T value;
        private Arithmetic(T value) {
            this.value = value;
        }

        abstract T add(T other);
        abstract T divide(AtomicInteger atomicInteger);
        T get() { return value; }
    }

    private static class BigIntegerArithmetic extends Arithmetic<BigInteger> {

        private BigIntegerArithmetic(BigInteger bigInteger) {
            super(bigInteger);
        }

        @Override
        public BigInteger add(BigInteger other) {
            return value.add(other);
        }

        @Override
        BigInteger divide(AtomicInteger atomicInteger) {
            return value.divide(BigInteger.valueOf(atomicInteger.get()));
        }
    }

    private static class BigDecimalArithmetic extends Arithmetic<BigDecimal> {

        private BigDecimalArithmetic(BigDecimal bigDecimal) {
            super(bigDecimal);
        }

        @Override
        public BigDecimal add(BigDecimal other) {
            return value.add(other);
        }

        @Override
        BigDecimal divide(AtomicInteger atomicInteger) {
            return value.divide(BigDecimal.valueOf(atomicInteger.get()), RoundingMode.HALF_EVEN);
        }
    }


}

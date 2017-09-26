//package com.nevex.investing.analyzer;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//
///**
// * Created by Mark Cunningham on 9/25/2017.
// */
//public class WeightCalculator {
//
//
//    public BigDecimal calculateWeight(BigDecimal value, BigDecimal middle, BigDecimal lowest, BigDecimal highest) {
//        if ( value.compareTo(lowest) <= 0) {
//            return NEGATIVE_ONE;
//        }
//        if ( value.compareTo(highest) >= 0) {
//            return ONE;
//        }
//        if ( value.compareTo(middle) == 0 ) {
//            return BigDecimal.ZERO;
//        }
//
//        BigDecimal rangeOther;
//
//        if ( value.compareTo(middle) < 0) {
//            rangeOther = lowest;
//        } else {
//            rangeOther = highest;
//        }
//        BigDecimal range = middle.abs().subtract(rangeOther.abs()).abs();
//        return value.divide(range, SCALE, RoundingMode.HALF_EVEN);
//    }
//
//
//}

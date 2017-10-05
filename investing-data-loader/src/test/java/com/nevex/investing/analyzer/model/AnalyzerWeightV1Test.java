package com.nevex.investing.analyzer.model;

import com.nevex.investing.model.Analyzer;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public class AnalyzerWeightV1Test {

    @Test
    public void testInBetweenCalculationIsCorrect() {
        AnalyzerWeightV1 weight = new AnalyzerWeightV1(Analyzer.PRICE_TO_EARNINGS_RATIO, BigDecimal.ONE, BigDecimal.TEN, 4.5);
        assertThat(weight.isAround(BigDecimal.valueOf(1.00001))).isTrue();
        assertThat(weight.isAround(BigDecimal.valueOf(3))).isTrue();
        assertThat(weight.isAround(BigDecimal.valueOf(7))).isTrue();
        assertThat(weight.isAround(BigDecimal.valueOf(10))).isTrue();


        assertThat(weight.isAround(BigDecimal.valueOf(1))).isFalse(); // the start is exclusive
        assertThat(weight.isAround(BigDecimal.valueOf(10.1))).isFalse(); // the start is exclusive

        // null out the start
        weight = new AnalyzerWeightV1(Analyzer.PRICE_TO_EARNINGS_RATIO, null, BigDecimal.TEN, 4.5);
        assertThat(weight.isAround(BigDecimal.valueOf(3))).isTrue();
    }


}

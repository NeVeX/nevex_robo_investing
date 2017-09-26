package com.nevex.investing.analyzer.model;

import com.nevex.investing.model.Analyzer;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Mark Cunningham on 9/25/2017.
 */
public class AnalyzerWeightsV2Test {

    @Test
    public void simplePositiveRangeTest() {

        BigDecimal middle = BigDecimal.valueOf(0);
        BigDecimal lowest = BigDecimal.valueOf(-5);
        BigDecimal highest = BigDecimal.valueOf(50);

        BigDecimal value = BigDecimal.valueOf(25);

        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1);
        double weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(0.5);
    }

    @Test
    public void simpleNegativeRangeTest() {
        BigDecimal middle = BigDecimal.valueOf(-10);
        BigDecimal lowest = BigDecimal.valueOf(-100);
        BigDecimal highest = BigDecimal.valueOf(5);

        BigDecimal value = BigDecimal.valueOf(-50);
        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1);
        double weight = analyzerWeightV2.calculateWeight(value);

        assertThat(weight).isEqualTo(-0.4444);
    }

    @Test
    public void earningsPerShare() {
        BigDecimal middle = BigDecimal.valueOf(0);
        BigDecimal lowest = BigDecimal.valueOf(-5);
        BigDecimal highest = BigDecimal.valueOf(50);

        BigDecimal value = BigDecimal.valueOf(20);
        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, true, 1); // invert the highest
        double weight = analyzerWeightV2.calculateWeight(value);

        assertThat(weight).isEqualTo(0.6);

        value = BigDecimal.valueOf(10);
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(0.8);

        // check the negative route
        value = BigDecimal.valueOf(-3);
        analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, true, 1); // invert the highest
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(-0.6);
    }

    @Test
    public void flipSignBit() {
        BigDecimal middle = BigDecimal.valueOf(100);
        BigDecimal lowest = BigDecimal.valueOf(50);
        BigDecimal highest = BigDecimal.valueOf(150);

        BigDecimal value = BigDecimal.valueOf(90);
        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, -1); // flip the sign bit (low scores are better here)
        double weight = analyzerWeightV2.calculateWeight(value);

        assertThat(weight).isEqualTo(0.2);

        // this basically is the inverse of the above, such that 0.2 will become 0.8
        analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, true, false, -1); // flip the sign bit and the invert for the lowest
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(0.8);
    }

    @Test
    public void makeSureLowestAndHighestAreHonoured() {
        BigDecimal middle = BigDecimal.valueOf(10);
        BigDecimal lowest = BigDecimal.valueOf(-5);
        BigDecimal highest = BigDecimal.valueOf(25);

        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, -1);

        BigDecimal value = BigDecimal.valueOf(90);
        double weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(1); // the value is far beyond the max

        value = BigDecimal.valueOf(25);
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(1); // the value is just on the max

        value = BigDecimal.valueOf(-5);
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(-1); // the value is just on the max (lowest)

        value = BigDecimal.valueOf(-40);
        weight = analyzerWeightV2.calculateWeight(value);
        assertThat(weight).isEqualTo(-1); // the value far beyond the (lowest)
    }

}

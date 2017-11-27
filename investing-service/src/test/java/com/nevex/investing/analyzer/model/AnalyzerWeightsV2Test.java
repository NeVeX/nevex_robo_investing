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
        BigDecimal lowest = BigDecimal.valueOf(-5);
        BigDecimal middle = BigDecimal.valueOf(10);
        BigDecimal highest = BigDecimal.valueOf(25);

        AnalyzerWeightV2 analyzerWeightV2 = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1);

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

    @Test
    public void makeSureStockPriceIsCorrect() {
        // (1, 'current-stock-price', 0.1, 0, 100, false, true, 1),
        AnalyzerWeightV2 stockPriceWeight = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, new BigDecimal("0.1"),
                new BigDecimal("0"), new BigDecimal("100"), false, true, 1);

        double calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(50));
        assertThat(calcWeight).isEqualTo(0.5005);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(90));
        assertThat(calcWeight).isEqualTo(0.1001);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(120));
        assertThat(calcWeight).isEqualTo(0.0);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(99));
        assertThat(calcWeight).isEqualTo(0.01);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(0.05));
        assertThat(calcWeight).isEqualTo(-0.5);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(-1.0));
        assertThat(calcWeight).isEqualTo(-1.0);

        calcWeight = stockPriceWeight.calculateWeight(new BigDecimal(-100.0));
        assertThat(calcWeight).isEqualTo(-1.0);

    }

    @Test
    public void makeSureVariousSignsAreOk() {

        BigDecimal lowest = new BigDecimal("-200");
        BigDecimal center = new BigDecimal("-100");
        BigDecimal highest = new BigDecimal("-10");

        AnalyzerWeightV2 allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-200"))).isEqualTo(-1.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-170"))).isEqualTo(-0.7);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-120"))).isEqualTo(-0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-70"))).isEqualTo(0.3333);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(0.8889);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-10"))).isEqualTo(1.0);

        // use negative direction
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, -1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-200"))).isEqualTo(1.0); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-170"))).isEqualTo(0.7); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-120"))).isEqualTo(0.2); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-70"))).isEqualTo(-0.3333); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(-0.8889); // sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-10"))).isEqualTo(-1.0); // sign changed

        // invert the lowest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-200"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-170"))).isEqualTo(-0.3); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-120"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-70"))).isEqualTo(0.3333);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(0.8889);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-10"))).isEqualTo(1.0);

        // invert the lowest and the highest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, true, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-200"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-170"))).isEqualTo(-0.3); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-120"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-70"))).isEqualTo(0.6667); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(0.1111); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-10"))).isEqualTo(0.0); // inverted

        // invert the lowest and the highest and with negative direction
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, true, -1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-200"))).isEqualTo(0.0); // inverted + negative now
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-170"))).isEqualTo(0.3); // inverted + negative now
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-120"))).isEqualTo(0.8); // inverted + negative now
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-70"))).isEqualTo(-0.6667); // inverted + negative now
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(-0.1111); // inverted + negative now
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-10"))).isEqualTo(0.0); // inverted + negative now


        /**
         * Change the analyzer - use simple positive weights
         */
        lowest = new BigDecimal("100");
        center = new BigDecimal("200");
        highest = new BigDecimal("300");

        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(-1.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("120"))).isEqualTo(-0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("180"))).isEqualTo(-0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("200"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("220"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("280"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("300"))).isEqualTo(1.0);

        // change the sign direction
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, -1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(1.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("120"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("180"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("200"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("220"))).isEqualTo(-0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("280"))).isEqualTo(-0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("300"))).isEqualTo(-1.0);

        // invert the lowest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("120"))).isEqualTo(-0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("180"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("200"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("220"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("280"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("300"))).isEqualTo(1.0);

        // invert the lowest and highest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, true, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("120"))).isEqualTo(-0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("180"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("200"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("220"))).isEqualTo(0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("280"))).isEqualTo(0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("300"))).isEqualTo(0.0); // inverted


        /**
         * Change the analyzer - use cross over weights (negative to positive
         */
        lowest = new BigDecimal("-100");
        center = new BigDecimal("0");
        highest = new BigDecimal("100");

        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(-1.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-80"))).isEqualTo(-0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(-0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("0"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("20"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("80"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(1.0);

        // change the sign direction
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, false, false, -1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(1.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-80"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("0"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("20"))).isEqualTo(-0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("80"))).isEqualTo(-0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(-1.0);

        // invert the lowest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, false, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-80"))).isEqualTo(-0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("0"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("20"))).isEqualTo(0.2);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("80"))).isEqualTo(0.8);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(1.0);

        // invert the lowest and highest
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, true, 1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-80"))).isEqualTo(-0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(-0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("0"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("20"))).isEqualTo(0.8); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("80"))).isEqualTo(0.2); // inverted
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(0.0); // inverted

        // invert the lowest and highest and change signs
        allNegativeWeights = new AnalyzerWeightV2(1, Analyzer.CURRENT_STOCK_PRICE, center, lowest, highest, true, true, -1);

        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-100"))).isEqualTo(0.0); // inverted and sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-80"))).isEqualTo(0.2); // inverted and sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("-20"))).isEqualTo(0.8); // inverted and sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("0"))).isEqualTo(0.0);
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("20"))).isEqualTo(-0.8); // inverted and sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("80"))).isEqualTo(-0.2); // inverted and sign changed
        assertThat(allNegativeWeights.calculateWeight(new BigDecimal("100"))).isEqualTo(0.0); // inverted and sign changed

    }

    @Test
    public void makeSureVariousRangesWorksOk() {

        BigDecimal lowest = BigDecimal.valueOf(-100);
        BigDecimal middle = BigDecimal.valueOf(0);
        BigDecimal highest = BigDecimal.valueOf(100);
        AnalyzerWeightV2 analyzer = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1); // normal

        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(100))).isEqualTo(1); // is the highest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-100))).isEqualTo(-1); // is the lowest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(0))).isEqualTo(0); // is the center

        // check exceeding the limit
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(200))).isEqualTo(1); // more than the highest; should constraint to 1 still
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-200))).isEqualTo(-1); // less than the lowest; should constraint to -1 still


        lowest = BigDecimal.valueOf(500);
        middle = BigDecimal.valueOf(750);
        highest = BigDecimal.valueOf(1000);
        analyzer = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1); // normal
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(1000))).isEqualTo(1); // is the highest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(500))).isEqualTo(-1); // is the lowest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(750))).isEqualTo(0); // is the center
        // check exceeding the limit
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(1100))).isEqualTo(1); // more than the highest; should constraint to 1 still
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(400))).isEqualTo(-1); // less than the lowest; should constraint to -1 still

        // All negatives
        lowest = BigDecimal.valueOf(-300);
        middle = BigDecimal.valueOf(-200);
        highest = BigDecimal.valueOf(-100);
        analyzer = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1); // normal
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-100))).isEqualTo(1); // is the highest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-300))).isEqualTo(-1); // is the lowest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-200))).isEqualTo(0); // is the center


        lowest = BigDecimal.valueOf(-1000);
        middle = BigDecimal.valueOf(-750);
        highest = BigDecimal.valueOf(-500);
        analyzer = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1); // normal
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-500))).isEqualTo(1); // is the highest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-1000))).isEqualTo(-1); // is the lowest
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-750))).isEqualTo(0); // is the center
        // check exceeding the limit
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-400))).isEqualTo(1); // more than the highest; should constraint to 1 still
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-1100))).isEqualTo(-1); // less than the lowest; should constraint to -1 still

    }

    @Test // This used to occur
    public void makeSureWhenRangeIsZeroThatItDoesNotBreak_bugTest() {

        // create an example with a zero range
        BigDecimal lowest = BigDecimal.valueOf(-100);
        BigDecimal middle = BigDecimal.valueOf(100);
        BigDecimal highest = BigDecimal.valueOf(100);
        AnalyzerWeightV2 analyzer = new AnalyzerWeightV2(1, Analyzer.PRICE_TO_EARNINGS_RATIO, middle, lowest, highest, false, false, 1);
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(200))).isEqualTo(1); // should not break (divide by zero exception)
        assertThat(analyzer.calculateWeight(BigDecimal.valueOf(-200))).isEqualTo(-1); // should not break (divide by zero exception)
    }

}

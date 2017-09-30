package com.nevex.investing.analyzer;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.Test;

/**
 * Created by Mark Cunningham on 9/27/2017.
 */
public class SimpleRegressionTest {

    @Test
    public void testSimpleRegression() {

        SimpleRegression regression = new SimpleRegression();
        regression.addData(1, 1);
        regression.addData(2, 3);
        regression.addData(3, 5);
        regression.addData(4, 1000);
//        regression.addData(5, 1000);
//        regression.addData(6, 10000);

        double slope = regression.getSlope();
        while ( Double.valueOf(Math.abs(slope)).intValue() > 0 ) {
            slope /= 10;
        }

        System.out.println("R: "+regression.getR()); // if 1 it's a perfect slope?
        System.out.println("R squared: "+regression.getRSquare()); // if 1 it's a perfect slope?
//        System.out.println("Intercept: "+regression.getIntercept());
        System.out.println("Slope: "+regression.getSlope()); // higher is better too - positive means say price increasing etc)
        System.out.println("Slope (constrained): "+slope); // higher is better too - positive means say price increasing etc)
        System.out.println("Significance: "+regression.getSignificance());
    }

}

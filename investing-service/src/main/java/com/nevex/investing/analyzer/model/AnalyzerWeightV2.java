package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.AnalyzerWeightEntityV2;
import com.nevex.investing.model.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/25/2017.
 */
public class AnalyzerWeightV2 {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerWeightV2.class);

    private final int SCALE = 4;
    private final BigDecimal ONE = BigDecimal.valueOf(1);
    private final int version;
    private final Analyzer analyzer;
    private final BigDecimal center;
    private final BigDecimal lowest;
    private final BigDecimal highest;
    private final boolean invertLowest;
    private final boolean invertHighest;
    private final int signDirection;

    public AnalyzerWeightV2(int version, Analyzer analyzer, BigDecimal center, BigDecimal lowest, BigDecimal highest, boolean invertLowest, boolean invertHighest, int signDirection) {
        this.version = version;
        this.analyzer = analyzer;
        this.center = center;
        this.lowest = lowest;
        this.highest = highest;
        this.invertLowest = invertLowest;
        this.invertHighest = invertHighest;
        this.signDirection = signDirection;
        validate();
    }

    private void validate() {

        if ( lowest.compareTo(center) > 0) {
            throw new IllegalStateException("Invalid analyzer weight found. Lowest cannot be greater than center for weight ["+this+"]");
        }
        if ( highest.compareTo(center) < 0 ) {
            throw new IllegalStateException("Invalid analyzer weight found. Highest cannot be less than center for weight ["+this+"]");
        }

    }

    public AnalyzerWeightV2(Analyzer analyzer, AnalyzerWeightEntityV2 entity) {
        this(entity.getVersion(), analyzer, entity.getCenter(), entity.getLowest(), entity.getHighest(), entity.getInvertLowest(), entity.getInvertHighest(), entity.getSignDirection());
    }

    public double calculateWeight(final BigDecimal inputValue) {
        if ( inputValue.compareTo(center) == 0 ) {
            return 0;
        }

        BigDecimal inputValueLimited; // the limited value (in case it exceeds limits)
        if ( inputValue.compareTo(lowest) <= 0) {
            inputValueLimited = lowest;
        } else if ( inputValue.compareTo(highest) >= 0) {
            inputValueLimited = highest;
        } else {
            inputValueLimited = inputValue;
        }

        BigDecimal valueConstrained;
        BigDecimal range;
        int signValue;
        boolean shouldInvert;
        if ( inputValueLimited.compareTo(center) < 0) {
            signValue = signDirection * -1;
            shouldInvert = invertLowest;
            valueConstrained = center.subtract(inputValueLimited).abs();
            if ( inputValueLimited.signum() < 0) { valueConstrained = valueConstrained.negate(); }
            range = center.subtract(lowest).abs();
        } else {
            signValue = signDirection;
            shouldInvert = invertHighest;
            valueConstrained = inputValueLimited.subtract(center).abs();
            if ( inputValueLimited.signum() < 0) { valueConstrained = valueConstrained.negate(); }
            range = highest.subtract(center).abs();
        }

        BigDecimal weight;
        if ( range.compareTo(BigDecimal.ZERO) == 0) { // special edge cases
            weight = ONE;
        } else {
            weight = valueConstrained.divide(range, SCALE, RoundingMode.HALF_EVEN).abs();
        }

        if ( shouldInvert ) {
            // TODO: this right? It used to be ONE.subtract...
            weight = ONE.subtract(weight);
        }
        // finally, flip the signs if need be
        double finalWeight = weight.multiply(BigDecimal.valueOf(signValue)).doubleValue();
        if ( finalWeight < -1.0 || finalWeight > 1.0) {
            LOGGER.warn("The final weight calculation is [{}] which is beyond +=1; the input value was [{}] to this weight [{}]", finalWeight, inputValue, this);
        }
        return finalWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerWeightV2 that = (AnalyzerWeightV2) o;
        return version == that.version &&
                analyzer == that.analyzer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, analyzer);
    }

    @Override
    public String toString() {
        return "AnalyzerWeightV2{" +
                "SCALE=" + SCALE +
                ", ONE=" + ONE +
                ", version=" + version +
                ", analyzer=" + analyzer +
                ", center=" + center +
                ", lowest=" + lowest +
                ", highest=" + highest +
                ", invertLowest=" + invertLowest +
                ", invertHighest=" + invertHighest +
                ", signDirection=" + signDirection +
                '}';
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public int getSignDirection() {
        return signDirection;
    }

    public BigDecimal getLowest() {
        return lowest;
    }

    public BigDecimal getHighest() {
        return highest;
    }

    public boolean isInvertLowest() {
        return invertLowest;
    }

    public boolean isInvertHighest() {
        return invertHighest;
    }

    public BigDecimal getCenter() {
        return center;
    }

    public int getVersion() {
        return version;
    }
}

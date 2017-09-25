package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.AnalyzerWeightEntity;
import com.nevex.investing.model.Analyzer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public class AnalyzerWeight implements Comparable<AnalyzerWeight> {

    private final Analyzer analyzer;
    private final BigDecimal start;
    private final BigDecimal end;
    private final double weight;

    public AnalyzerWeight(Analyzer analyzer, BigDecimal start, BigDecimal end, double weight) {
        this.analyzer = analyzer;
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public AnalyzerWeight(AnalyzerWeightEntity entity) {
        this(Analyzer.fromTitle(entity.getName()).get(), entity.getStart(), entity.getEnd(), entity.getWeight());
    }

    public boolean isAround(BigDecimal value) {
        boolean isAfterStart = start == null || start.compareTo(value) < 0;
        boolean isBeforeEnd = end.compareTo(value) >= 0;
        return isAfterStart && isBeforeEnd;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public BigDecimal getStart() {
        return start;
    }

    public BigDecimal getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public int compareTo(AnalyzerWeight o) {
        return ObjectUtils.compare(start, o.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerWeight weight = (AnalyzerWeight) o;
        return analyzer == weight.analyzer &&
                Objects.equals(start, weight.start) &&
                Objects.equals(end, weight.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(analyzer, start, end);
    }

    @Override
    public String toString() {
        return "AnalyzerWeight{" +
                "analyzer=" + analyzer.getTitle() +
                ", start=" + start +
                ", end=" + end +
                ", weight=" + weight +
                '}';
    }
}

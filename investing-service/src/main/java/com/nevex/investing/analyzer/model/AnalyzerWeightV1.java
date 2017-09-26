package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.AnalyzerWeightEntityV1;
import com.nevex.investing.model.Analyzer;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public class AnalyzerWeightV1 implements Comparable<AnalyzerWeightV1> {

    private final Analyzer analyzer;
    private final BigDecimal start;
    private final BigDecimal end;
    private final double weight;

    public AnalyzerWeightV1(Analyzer analyzer, BigDecimal start, BigDecimal end, double weight) {
        this.analyzer = analyzer;
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public AnalyzerWeightV1(AnalyzerWeightEntityV1 entity) {
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
    public int compareTo(AnalyzerWeightV1 o) {
        return ObjectUtils.compare(start, o.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerWeightV1 weight = (AnalyzerWeightV1) o;
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
        return "AnalyzerWeightV1{" +
                "analyzer=" + analyzer.getTitle() +
                ", start=" + start +
                ", end=" + end +
                ", weight=" + weight +
                '}';
    }
}

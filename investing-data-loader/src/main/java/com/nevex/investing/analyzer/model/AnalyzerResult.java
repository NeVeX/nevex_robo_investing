package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public class AnalyzerResult {

    private final int tickerId;
    private final String name;
    private final double weight;
    private final LocalDate date;

    public AnalyzerResult(int tickerId, String name, double weight) {
        this(tickerId, name, weight, LocalDate.now());
    }

    public AnalyzerResult(TickerAnalyzerEntity entity) {
        this(entity.getTickerId(), entity.getName(), entity.getWeight(), entity.getDate());
    }

    public AnalyzerResult(int tickerId, String name, double weight, LocalDate date) {
        if (StringUtils.isBlank(name)) { throw new IllegalArgumentException("Provided name is null"); }
        if (date == null) { throw new IllegalArgumentException("Provided date is null"); }
        this.tickerId = tickerId;
        this.name = name;
        this.weight = weight;
        this.date = date;
    }

    public int getTickerId() {
        return tickerId;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerResult that = (AnalyzerResult) o;
        return tickerId == that.tickerId &&
                Double.compare(that.weight, weight) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, name, weight, date);
    }

    @Override
    public String toString() {
        return "AnalyzerResult{" +
                "tickerId=" + tickerId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", date=" + date +
                '}';
    }
}

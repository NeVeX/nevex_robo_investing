package com.nevex.investing.analyzer.model;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

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
}

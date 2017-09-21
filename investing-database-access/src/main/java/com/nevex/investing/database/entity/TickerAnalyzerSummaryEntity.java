package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/3/2017.
 */
@Entity
@Table(schema = "investing", name = "ticker_analyzers_summary_v1", uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date"}))
public class TickerAnalyzerSummaryEntity implements MergeableEntity<TickerAnalyzerSummaryEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "total_analyzers")
    private int totalAnalyzers;
    @Column(name = "average_weight")
    private double averageWeight;
    @Column(name = "adjusted_weight")
    private double adjustedWeight;

    public TickerAnalyzerSummaryEntity() { }

    public TickerAnalyzerSummaryEntity(int tickerId, LocalDate date, int totalAnalyzers, double averageWeight, double adjustedWeight) {
        this.tickerId = tickerId;
        this.date = date;
        this.totalAnalyzers = totalAnalyzers;
        this.averageWeight = averageWeight;
        this.adjustedWeight = adjustedWeight;
    }

    @Override
    public void merge(TickerAnalyzerSummaryEntity other) {
        this.totalAnalyzers = other.totalAnalyzers;
        this.averageWeight = other.averageWeight;
        this.adjustedWeight = other.adjustedWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerAnalyzerSummaryEntity that = (TickerAnalyzerSummaryEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, date);
    }

    @Override
    public String toString() {
        return "TickerAnalyzerSummaryEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", date=" + date +
                ", totalAnalyzers=" + totalAnalyzers +
                ", averageWeight=" + averageWeight +
                ", adjustedWeight=" + adjustedWeight +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTickerId() {
        return tickerId;
    }

    public void setTickerId(int tickerId) {
        this.tickerId = tickerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalAnalyzers() {
        return totalAnalyzers;
    }

    public void setTotalAnalyzers(int totalAnalyzers) {
        this.totalAnalyzers = totalAnalyzers;
    }

    public double getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(double averageWeight) {
        this.averageWeight = averageWeight;
    }

    public double getAdjustedWeight() {
        return adjustedWeight;
    }

    public void setAdjustedWeight(double adjustedWeight) {
        this.adjustedWeight = adjustedWeight;
    }

}

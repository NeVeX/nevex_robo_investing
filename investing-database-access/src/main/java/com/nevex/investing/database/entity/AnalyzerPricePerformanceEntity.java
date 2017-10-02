package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Entity
@Table(schema = "investing", name = "analyzer_price_performance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date"}))
public class AnalyzerPricePerformanceEntity implements MergeableEntity<AnalyzerPricePerformanceEntity> {

    public static final String DATE_COL = "date";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = DATE_COL, columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "price_direction_as_expected")
    private boolean priceDirectionAsExpected;
    @Column(name = "price_difference")
    private BigDecimal priceDifference;
    @Column(name = "percent_difference")
    private BigDecimal percentDifference;

    @Override
    public void merge(AnalyzerPricePerformanceEntity other) {
        this.priceDifference = other.priceDifference;
        this.priceDirectionAsExpected = other.priceDirectionAsExpected;
        this.percentDifference = other.percentDifference;
    }

    public AnalyzerPricePerformanceEntity() { }

    public AnalyzerPricePerformanceEntity(int tickerId, LocalDate date, boolean priceDirectionAsExpected, BigDecimal priceDifference, BigDecimal percentDifference) {
        this.tickerId = tickerId;
        this.date = date;
        this.priceDirectionAsExpected = priceDirectionAsExpected;
        this.priceDifference = priceDifference;
        this.percentDifference = percentDifference;
    }

    @Override
    public String toString() {
        return "AnalyzerPricePerformanceEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", date=" + date +
                ", priceDirectionAsExpected=" + priceDirectionAsExpected +
                ", priceDifference=" + priceDifference +
                ", percentDifference=" + percentDifference +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerPricePerformanceEntity that = (AnalyzerPricePerformanceEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, date);
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

    public boolean getPriceDirectionAsExpected() {
        return priceDirectionAsExpected;
    }

    public void setPriceDirectionAsExpected(boolean priceDirectionAsExpected) {
        this.priceDirectionAsExpected = priceDirectionAsExpected;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public BigDecimal getPercentDifference() {
        return percentDifference;
    }

    public void setPercentDifference(BigDecimal percentDifference) {
        this.percentDifference = percentDifference;
    }
}

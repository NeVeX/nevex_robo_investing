package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/3/2017.
 */
@Entity
@Table(schema = "investing", name = "ticker_analyzers_v1", uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date", "name"}))
public class TickerAnalyzerEntity implements MergeableEntity<TickerAnalyzerEntity> {

    public final static String DATE_COL = "date";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = DATE_COL, columnDefinition = "DATE")
    private LocalDate date;
    @Column(name = "name")
    private String name;
    @Column(name = "weight")
    private double weight;

    public TickerAnalyzerEntity() { }

    public TickerAnalyzerEntity(int tickerId, LocalDate date, String name, double weight) {
        this.tickerId = tickerId;
        this.date = date;
        this.name = name;
        this.weight = weight;
    }

    @Override
    public void merge(TickerAnalyzerEntity other) {
        this.weight = other.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerAnalyzerEntity that = (TickerAnalyzerEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(date, that.date) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, date, name);
    }

    @Override
    public String toString() {
        return "TickerAnalyzerEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", weight=" + weight +
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


}

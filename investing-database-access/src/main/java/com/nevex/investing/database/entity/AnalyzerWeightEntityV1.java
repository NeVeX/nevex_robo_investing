package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "analyzer_weights_v1",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "start", "end"}))
public class AnalyzerWeightEntityV1 {

    public static final String ID_COL = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID_COL)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "start")
    private BigDecimal start;
    @Column(name = "end")
    private BigDecimal end;
    @Column(name = "weight")
    private double weight;

    public AnalyzerWeightEntityV1() { }

    public AnalyzerWeightEntityV1(String name, BigDecimal start, BigDecimal end, double weight) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "AnalyzerWeightEntityV1{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerWeightEntityV1 that = (AnalyzerWeightEntityV1) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, start, end);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getStart() {
        return start;
    }

    public void setStart(BigDecimal start) {
        this.start = start;
    }

    public BigDecimal getEnd() {
        return end;
    }

    public void setEnd(BigDecimal end) {
        this.end = end;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}

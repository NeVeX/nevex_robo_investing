package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "analyzer_weights_v2", uniqueConstraints = @UniqueConstraint(columnNames = {"version", "name"}))
public class AnalyzerWeightEntityV2 {

    public static final String ID_COL = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID_COL)
    private int id;
    @Column(name = "version")
    private int version;
    @Column(name = "name")
    private String name;
    @Column(name = "center")
    private BigDecimal center;
    @Column(name = "lowest")
    private BigDecimal lowest;
    @Column(name = "highest")
    private BigDecimal highest;
    @Column(name = "invert_lowest")
    private boolean invertLowest;
    @Column(name = "invert_highest")
    private boolean invertHighest;
    @Column(name = "sign_direction")
    private short signDirection;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyzerWeightEntityV2 that = (AnalyzerWeightEntityV2) o;
        return version == that.version &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, name);
    }

    @Override
    public String toString() {
        return "AnalyzerWeightEntityV2{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", center=" + center +
                ", lowest=" + lowest +
                ", highest=" + highest +
                ", invertLowest=" + invertLowest +
                ", invertHighest=" + invertHighest +
                ", signDirection=" + signDirection +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCenter() {
        return center;
    }

    public void setCenter(BigDecimal center) {
        this.center = center;
    }

    public BigDecimal getLowest() {
        return lowest;
    }

    public void setLowest(BigDecimal lowest) {
        this.lowest = lowest;
    }

    public BigDecimal getHighest() {
        return highest;
    }

    public void setHighest(BigDecimal highest) {
        this.highest = highest;
    }

    public boolean getInvertLowest() {
        return invertLowest;
    }

    public void setInvertLowest(boolean invertLowest) {
        this.invertLowest = invertLowest;
    }

    public boolean getInvertHighest() {
        return invertHighest;
    }

    public void setInvertHighest(boolean invertHighest) {
        this.invertHighest = invertHighest;
    }

    public short getSignDirection() {
        return signDirection;
    }

    public void setSignDirection(short signDirection) {
        this.signDirection = signDirection;
    }
}

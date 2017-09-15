package com.nevex.investing.database.entity;

import com.nevex.investing.model.TimePeriod;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_price_change_tracker",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "period_name"}))
public class StockPriceChangeTrackerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "period_name")
    private String periodName;
    @Column(name = "open_avg")
    private BigDecimal openAvg;
    @Column(name = "high_avg")
    private BigDecimal highAvg;
    @Column(name = "low_avg")
    private BigDecimal lowAvg;
    @Column(name = "close_avg")
    private BigDecimal closeAvg;
    @Column(name = "volume_avg")
    private long volumeAvg;

    public StockPriceChangeTrackerEntity() {}

    public StockPriceChangeTrackerEntity(int tickerId, String periodName, BigDecimal openAvg, BigDecimal highAvg, BigDecimal lowAvg, BigDecimal closeAvg, long volumeAvg) {
        this.tickerId = tickerId;
        this.periodName = periodName;
        this.openAvg = openAvg;
        this.highAvg = highAvg;
        this.lowAvg = lowAvg;
        this.closeAvg = closeAvg;
        this.volumeAvg = volumeAvg;
    }

    public void merge(StockPriceChangeTrackerEntity other) {
        this.openAvg = other.openAvg;
        this.highAvg = other.highAvg;
        this.lowAvg = other.lowAvg;
        this.closeAvg = other.closeAvg;
        this.volumeAvg = other.volumeAvg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceChangeTrackerEntity that = (StockPriceChangeTrackerEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(periodName, that.periodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, periodName);
    }

    @Override
    public String toString() {
        return "StockPriceChangeTrackerEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", periodName='" + periodName + '\'' +
                ", openAvg=" + openAvg +
                ", highAvg=" + highAvg +
                ", lowAvg=" + lowAvg +
                ", closeAvg=" + closeAvg +
                ", volumeAvg=" + volumeAvg +
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

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public BigDecimal getOpenAvg() {
        return openAvg;
    }

    public void setOpenAvg(BigDecimal openAvg) {
        this.openAvg = openAvg;
    }

    public BigDecimal getHighAvg() {
        return highAvg;
    }

    public void setHighAvg(BigDecimal highAvg) {
        this.highAvg = highAvg;
    }

    public BigDecimal getLowAvg() {
        return lowAvg;
    }

    public void setLowAvg(BigDecimal lowAvg) {
        this.lowAvg = lowAvg;
    }

    public BigDecimal getCloseAvg() {
        return closeAvg;
    }

    public void setCloseAvg(BigDecimal closeAvg) {
        this.closeAvg = closeAvg;
    }

    public long getVolumeAvg() {
        return volumeAvg;
    }

    public void setVolumeAvg(long volumeAvg) {
        this.volumeAvg = volumeAvg;
    }
}

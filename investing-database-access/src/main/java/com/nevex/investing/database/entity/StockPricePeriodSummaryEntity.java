package com.nevex.investing.database.entity;

import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.model.TimePeriod;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_price_period_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "period_name", "date"}))
public class StockPricePeriodSummaryEntity implements MergeableEntity<StockPricePeriodSummaryEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "period_name")
    private String periodName;
    @Column(name = "date", columnDefinition = "DATE")
    private LocalDate date;
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
    @Column(name = "open_lowest")
    private BigDecimal openLowest;
    @Column(name = "open_highest")
    private BigDecimal openHighest;
    @Column(name = "highest")
    private BigDecimal highest;
    @Column(name = "lowest")
    private BigDecimal lowest;
    @Column(name = "close_lowest")
    private BigDecimal closeLowest;
    @Column(name = "close_highest")
    private BigDecimal closeHighest;
    @Column(name = "volume_highest")
    private long volumeHighest;
    @Column(name = "volume_lowest")
    private long volumeLowest;

    public StockPricePeriodSummaryEntity() {}

    public StockPricePeriodSummaryEntity(int tickerId, TimePeriod timePeriod, StockPriceSummary summary) {
        this(tickerId, timePeriod, summary, LocalDate.now());
    }

    public StockPricePeriodSummaryEntity(int tickerId, TimePeriod timePeriod, StockPriceSummary summary, LocalDate date) {
        this.tickerId = tickerId;
        this.date = date;
        this.periodName = timePeriod.getTitle();
        this.openAvg = summary.getOpenAvg();
        this.openLowest = summary.getOpenLowest();
        this.openHighest = summary.getOpenHighest();

        this.highAvg = summary.getHighAvg();
        this.highest = summary.getHighest();

        this.lowAvg = summary.getLowAvg();
        this.lowest = summary.getLowest();

        this.closeAvg = summary.getCloseAvg();
        this.closeHighest = summary.getCloseHighest();
        this.closeLowest = summary.getCloseLowest();

        this.volumeAvg = summary.getVolumeAvg();
        this.volumeHighest = summary.getVolumeHighest();
        this.volumeLowest = summary.getVolumeLowest();
    }

    @Override
    public void merge(StockPricePeriodSummaryEntity other) {
        this.openAvg = other.openAvg;
        this.openLowest = other.openLowest;
        this.openHighest = other.openHighest;

        this.highAvg = other.highAvg;
        this.highest = other.highest;

        this.lowAvg = other.lowAvg;
        this.lowest = other.lowest;

        this.closeAvg = other.closeAvg;
        this.closeLowest = other.closeLowest;
        this.closeHighest = other.closeHighest;

        this.volumeAvg = other.volumeAvg;
        this.volumeLowest = other.volumeLowest;
        this.volumeHighest = other.volumeHighest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPricePeriodSummaryEntity that = (StockPricePeriodSummaryEntity) o;
        return tickerId == that.tickerId &&
                Objects.equals(periodName, that.periodName) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId, periodName, date);
    }

    @Override
    public String toString() {
        return "StockPricePeriodSummaryEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", periodName='" + periodName + '\'' +
                ", date=" + date +
                ", openAvg=" + openAvg +
                ", highAvg=" + highAvg +
                ", lowAvg=" + lowAvg +
                ", closeAvg=" + closeAvg +
                ", volumeAvg=" + volumeAvg +
                ", openLowest=" + openLowest +
                ", openHighest=" + openHighest +
                ", highest=" + highest +
                ", lowest=" + lowest +
                ", closeLowest=" + closeLowest +
                ", closeHighest=" + closeHighest +
                ", volumeHighest=" + volumeHighest +
                ", volumeLowest=" + volumeLowest +
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public BigDecimal getOpenLowest() {
        return openLowest;
    }

    public void setOpenLowest(BigDecimal openLowest) {
        this.openLowest = openLowest;
    }

    public BigDecimal getOpenHighest() {
        return openHighest;
    }

    public void setOpenHighest(BigDecimal openHighest) {
        this.openHighest = openHighest;
    }

    public BigDecimal getHighest() {
        return highest;
    }

    public void setHighest(BigDecimal highest) {
        this.highest = highest;
    }

    public BigDecimal getLowest() {
        return lowest;
    }

    public void setLowest(BigDecimal lowest) {
        this.lowest = lowest;
    }

    public BigDecimal getCloseLowest() {
        return closeLowest;
    }

    public void setCloseLowest(BigDecimal closeLowest) {
        this.closeLowest = closeLowest;
    }

    public BigDecimal getCloseHighest() {
        return closeHighest;
    }

    public void setCloseHighest(BigDecimal closeHighest) {
        this.closeHighest = closeHighest;
    }

    public long getVolumeHighest() {
        return volumeHighest;
    }

    public void setVolumeHighest(long volumeHighest) {
        this.volumeHighest = volumeHighest;
    }

    public long getVolumeLowest() {
        return volumeLowest;
    }

    public void setVolumeLowest(long volumeLowest) {
        this.volumeLowest = volumeLowest;
    }
}

package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Entity
@Table(schema = "investing", name = "ticker_fundamentals_sync",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id"}))
public class TickerFundamentalsSyncEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "initial_download_nano")
    private long initialDownloadNano;
    @Column(name = "last_update_id")
    private Long lastUpdateId; // can be null

    public TickerFundamentalsSyncEntity() { }

    public TickerFundamentalsSyncEntity(int tickerId, long initialDownloadNano) {
        this.tickerId = tickerId;
        this.initialDownloadNano = initialDownloadNano;
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

    public long getInitialDownloadNano() {
        return initialDownloadNano;
    }

    public void setInitialDownloadNano(long initialDownloadNano) {
        this.initialDownloadNano = initialDownloadNano;
    }

    public Long getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(Long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerFundamentalsSyncEntity that = (TickerFundamentalsSyncEntity) o;
        return tickerId == that.tickerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tickerId);
    }
}

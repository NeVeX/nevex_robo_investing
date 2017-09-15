package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 9/3/2017.
 */
@Entity
@Table(schema = "investing", name = "ticker_to_cik")
public class TickerToCikEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ticker_id")
    private int tickerId;
    @Column(name = "cik")
    private String cik;
    @Column(name = "updated_timestamp")
    private OffsetDateTime updatedTimestamp;

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

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public OffsetDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(OffsetDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return "TickerToCikEntity{" +
                "id=" + id +
                ", tickerId=" + tickerId +
                ", cik='" + cik + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}

//package com.nevex.investing.database.entity;
//
//import javax.persistence.*;
//import java.time.OffsetDateTime;
//
///**
// * Created by Mark Cunningham on 9/4/2017.
// */
//@Entity
//@Table(schema = "investing", name = "ticker_fundamentals_sync")
//public class TickerFundamentalsSyncEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private int id;
//    @Column(name = "timestamp", columnDefinition = "timestamptz")
//    private OffsetDateTime timestamp;
//    @Column(name = "nanoSeconds")
//    private long nanoSeconds;
//
//    public TickerFundamentalsSyncEntity(long nanoSeconds) {
//        this.timestamp = OffsetDateTime.now();
//        this.nanoSeconds = nanoSeconds;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public OffsetDateTime getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(OffsetDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public long getNanoSeconds() {
//        return nanoSeconds;
//    }
//
//    public void setNanoSeconds(long nanoSeconds) {
//        this.nanoSeconds = nanoSeconds;
//    }
//}

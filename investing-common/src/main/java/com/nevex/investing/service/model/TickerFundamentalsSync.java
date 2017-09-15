package com.nevex.investing.service.model;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerFundamentalsSync {

    private final int id;
    private final int tickerId;
    private final long initialDownloadNano;
    private final String lastUpdateId;

    public TickerFundamentalsSync(int id, int tickerId, long initialDownloadNano, String lastUpdateId) {
        this.id = id;
        this.tickerId = tickerId;
        this.initialDownloadNano = initialDownloadNano;
        this.lastUpdateId = lastUpdateId;
    }

    public int getId() {
        return id;
    }

    public int getTickerId() {
        return tickerId;
    }

    public long getInitialDownloadNano() {
        return initialDownloadNano;
    }

    public String getLastUpdateId() {
        return lastUpdateId;
    }
}

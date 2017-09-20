package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public final class StockPriceUpdateEvent implements Event {

    private final int tickerId;

    public StockPriceUpdateEvent(int tickerId) {
        this.tickerId = tickerId;
    }

    public int getTickerId() {
        return tickerId;
    }
}

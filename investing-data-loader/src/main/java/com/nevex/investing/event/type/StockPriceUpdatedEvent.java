package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used when there is a stock price update
 */
public final class StockPriceUpdatedEvent implements Event {

    private final int tickerId;

    public StockPriceUpdatedEvent(int tickerId) {
        this.tickerId = tickerId;
    }

    public int getTickerId() {
        return tickerId;
    }
}

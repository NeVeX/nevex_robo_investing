package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
class TickerBasedEvent implements Event {

    private final int tickerId;

    protected TickerBasedEvent(int tickerId) {
        this.tickerId = tickerId;
    }

    public int getTickerId() {
        return tickerId;
    }

}

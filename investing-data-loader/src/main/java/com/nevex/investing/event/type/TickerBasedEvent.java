package com.nevex.investing.event.type;

import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
class TickerBasedEvent implements Event {

    private final int tickerId;
    private final LocalDate asOfDate;

    protected TickerBasedEvent(int tickerId, LocalDate asOfDate) {
        this.tickerId = tickerId;
        this.asOfDate = asOfDate;
    }

    public int getTickerId() {
        return tickerId;
    }

    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    @Override
    public int getId() {
        return tickerId;
    }
}

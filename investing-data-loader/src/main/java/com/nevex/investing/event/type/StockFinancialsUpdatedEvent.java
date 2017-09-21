package com.nevex.investing.event.type;

import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used when there is a stock price update
 */
public final class StockFinancialsUpdatedEvent extends TickerBasedEvent {

    public StockFinancialsUpdatedEvent(int tickerId, LocalDate asOfDate) {
        super(tickerId, asOfDate);
    }
}

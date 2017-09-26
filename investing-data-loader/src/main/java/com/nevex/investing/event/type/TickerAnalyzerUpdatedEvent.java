package com.nevex.investing.event.type;

import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used an analyzer has been updated
 */
public final class TickerAnalyzerUpdatedEvent extends TickerBasedEvent {

    public TickerAnalyzerUpdatedEvent(int tickerId, LocalDate asOfDate) {
        super(tickerId, asOfDate);
    }

}

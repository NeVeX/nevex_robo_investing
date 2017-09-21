package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used when there is a stock price update
 */
public final class TickerAnalyzerUpdatedEvent extends TickerBasedEvent {

    public TickerAnalyzerUpdatedEvent(int tickerId) {
        super(tickerId);
    }

}

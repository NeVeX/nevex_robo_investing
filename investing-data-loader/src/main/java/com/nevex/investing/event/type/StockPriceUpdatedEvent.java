package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used when there is a stock price update
 */
public final class StockPriceUpdatedEvent extends TickerBasedEvent {

    public StockPriceUpdatedEvent(int tickerId) {
        super(tickerId);
    }

}

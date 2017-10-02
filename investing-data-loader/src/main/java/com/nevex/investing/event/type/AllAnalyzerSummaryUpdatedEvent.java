package com.nevex.investing.event.type;

import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/19/2017.
 * <br>This event is used when a summary analyzer is performed for a ticker
 */
public final class AllAnalyzerSummaryUpdatedEvent extends TickerBasedEvent {

    public AllAnalyzerSummaryUpdatedEvent(int tickerId, LocalDate asOfDate) {
        super(tickerId, asOfDate);
    }

}

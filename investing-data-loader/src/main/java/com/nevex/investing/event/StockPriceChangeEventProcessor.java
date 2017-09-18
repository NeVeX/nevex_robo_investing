package com.nevex.investing.event;

import com.nevex.investing.event.type.StockPriceUpdateConsumer;

import java.util.Set;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class StockPriceChangeEventProcessor extends EventProcessor<StockPriceUpdateConsumer, Integer> {

    public StockPriceChangeEventProcessor(Set<StockPriceUpdateConsumer> stockPriceUpdateConsumers) {
        super(stockPriceUpdateConsumers, StockPriceUpdateConsumer.class);
    }

    @Override
    String getName() {
        return "stock-price-change-event-processor";
    }
}

package com.nevex.investing.event;

import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;

import java.util.Set;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class DailyStockPriceEventProcessor extends EventProcessor<DailyStockPriceUpdateConsumer, Integer> {

    public DailyStockPriceEventProcessor(Set<DailyStockPriceUpdateConsumer> dailyStockPriceConsumers) {
        super(dailyStockPriceConsumers, DailyStockPriceUpdateConsumer.class);
    }

    @Override
    String getName() {
        return "daily-stock-price-event-processor";
    }
}

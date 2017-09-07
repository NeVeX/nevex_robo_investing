package com.nevex.investing.event.type;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public interface DailyStockPriceUpdateProcessor {

    void update(int tickerId);

}

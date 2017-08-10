package com.nevex.roboinvesting.dataloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class CurrentStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(CurrentStockPriceLoader.class);
    private final AtomicBoolean isUnlockedFromDataLoaders = new AtomicBoolean(false);

    @Override
    int orderNumber() {
        return DataLoaderOrder.STOCK_PRICE_CURRENT_LOADER;
    }

    @Override
    void doWork() {
        // unlock this loader
        isUnlockedFromDataLoaders.set(true);
        LOGGER.info("Unlocked the lock so this loader can now fetch current price data on it's schedule");
    }

    // Run this Monday to Friday, at 8pm
    @Scheduled(cron = "* 0 0 20 * * MON-FRI", zone = "America/Los_Angeles")
    void getAllCurrentPrices() {
        LOGGER.info("Current prices job has started!");


        LOGGER.info("Current prices job has finished!");
    }
}

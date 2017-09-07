package com.nevex.investing.event;

import com.nevex.investing.event.type.DailyStockPriceUpdateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class EventManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final BlockingQueue<Integer> dailyStockPriceUpdateQueue = new ArrayBlockingQueue<>(1000);
    private Set<DailyStockPriceUpdateProcessor> dailyStockPriceProcessors = new HashSet<>();
    private final ExecutorService dailyStockPriceExecutor = Executors.newSingleThreadExecutor();

    public EventManager(Set<DailyStockPriceUpdateProcessor> dailyStockPriceProcessors) {

        if ( dailyStockPriceProcessors != null && !dailyStockPriceProcessors.isEmpty()) {
            this.dailyStockPriceProcessors.addAll(dailyStockPriceProcessors);
            dailyStockPriceExecutor.submit(this::processStockDailyQueue);
            dailyStockPriceExecutor.shutdown();
            LOGGER.info("A total of [{}] dailyStockPriceExecutors are registered", dailyStockPriceProcessors.size());
        }
    }

    /**
     * Inserts the ticker into the daily stock price queue to be processed later
     */
    public boolean addDailyStockPriceUpdateEvent(int tickerId) {
        try {
            dailyStockPriceUpdateQueue.put(tickerId);
            return true;
        } catch (InterruptedException interEx) {
            LOGGER.error("Could not add ticker into event queue, the thread was interrupted. Message: {}", interEx.getMessage());
            return false;
        }
    }

    private void processStockDailyQueue() {
        try {
            while ( !Thread.currentThread().isInterrupted()) {
                final int tickerId = dailyStockPriceUpdateQueue.take();
                try {
                    LOGGER.info("Dequeue ticker [{}] and will now invoke all dailyStockPriceUpdateProcessors", tickerId);
                    dailyStockPriceProcessors.stream().forEach(p -> p.update(tickerId));
                } catch (Exception e) {
                    LOGGER.error("Exception raised while processing ticker [{}] for dailyStockPriceUpdateQueue queue - will ignore and continue. Reason: {}", tickerId, e.getMessage());
                }
            }
            LOGGER.warn("The dailyStockPriceUpdateProcessors consumer executor will not process any more data");
        } catch (InterruptedException interEx) {
            LOGGER.error("An InterruptedException occurred while waiting to take elements from the stock daily queue. Will not continue. Message: {}", interEx.getMessage());
        }
    }

    @PreDestroy
    void preDestroy() {
        if ( dailyStockPriceExecutor != null ) {
            dailyStockPriceExecutor.shutdownNow();
        }
    }

}

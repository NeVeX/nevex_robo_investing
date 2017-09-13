package com.nevex.investing.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
abstract class EventProcessor<C extends Consumer<D>, D> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);
    private final BlockingQueue<D> queue;
    private final Set<C> consumers = new HashSet<>();
    private final ExecutorService executorService;

    abstract String getName();

    EventProcessor(Set<C> consumers) {
        this(consumers, 1000);
    }

    EventProcessor(Set<C> newConsumers, int queueSize) {
        queue = new ArrayBlockingQueue<>(queueSize);
        executorService = Executors.newSingleThreadExecutor(); // for now...

        if ( newConsumers != null && !newConsumers.isEmpty()) {
            consumers.addAll(newConsumers);
            executorService.submit(this::processQueue);
            executorService.shutdown();
            LOGGER.info("A total of [{}] consumers are registered for event processor [{}] ", consumers.size(), getName());
        }
    }

    private void processQueue() {
        try {
            while ( !Thread.currentThread().isInterrupted()) {
                final D data = queue.take();
                try {
                    LOGGER.debug("Dequeue'd data [{}] and will now invoke all [{}] consumers", data, getName());
                    consumers.stream().forEach(c -> c.accept(data));
                } catch (Exception e) {
                    LOGGER.error("Exception raised while processing data [{}] for [{}] - will ignore and continue. Reason: {}",
                            data, getName(), e);
                }
            }
            LOGGER.warn("The [{}] will not process any more data since the thread was interrupted", getName());
        } catch (InterruptedException interEx) {
            LOGGER.error("An interrupted exception occurred while waiting to take elements from the [{}] queue - this processor will not continue. Message: {}",
                    getName(), interEx.getMessage());
        }
    }

    @PreDestroy
    void preDestroy() {
        if ( executorService != null ) {
            executorService.shutdownNow();
        }
        LOGGER.warn("Shutting down the executor service for [{}]", getName());
    }

    /**
     * Inserts the data into the queue to be processed by all registered consumers.
     * Returns true of false to denote if the message was placed on the queue
     */
    public boolean addEvent(D dataEntry) {

        if ( consumers.isEmpty()) { return false; }

        try {
            queue.put(dataEntry);
            return true;
        } catch (InterruptedException interEx) {
            LOGGER.error("Could not add event data [{}] into the queue for [{}], the thread was interrupted. Message: {}", dataEntry, getName(), interEx.getMessage());
            return false;
        }
    }

}

package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mark Cunningham on 9/24/2017.
 */
class EventQueue {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventQueue.class);
    private final BlockingQueue<Event> queue;
    private final ExecutorService executorService;
    private final Set<EventConsumer> consumers;
    private final Class<Event> eventType;

    public EventQueue(int queueSize, Class<Event> eventType, Set<EventConsumer> consumers) {
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.executorService = Executors.newSingleThreadExecutor();
        this.consumers = consumers;
        this.eventType = eventType;
        init();
    }

    private void init() {
        if ( consumers.isEmpty() ) {
            LOGGER.warn("Not starting the queue for event type [{}] since there are no event consumers setup", eventType);
            return;
        }
        executorService.submit(this::continuallyProcessEventQueue);
        executorService.shutdown();
        LOGGER.info("The event queue has been [{}] started with [{}] consumers - will accept events", eventType, consumers.size());
    }

    void addEvent(Event event) throws Exception {
        queue.put(event);
    }

    void destroy() {
        if ( executorService != null ) {
            executorService.shutdownNow();
        }
    }

    Set<EventConsumer> getConsumers() { return consumers; }

    private void continuallyProcessEventQueue() {
        try {
            while ( !Thread.currentThread().isInterrupted()) {
                final Event event = queue.take();
                // let's send the event to all of it's consumers
                LOGGER.debug("Dequeue'd event [{}] and will now invoke all it's [{}] consumers", event, consumers.size());
                consumers.stream().forEach(consumer -> {
                    try {
                        consumer.accept(event);
                    } catch (Exception e) {
                        LOGGER.error("Exception raised while processing event [{}] in consumer [{}] - will ignore and continue.", event, consumer.getConsumerName(), e);
                    }
                });
            }
            LOGGER.warn("The event manager will not process any more data since the thread was interrupted");
        } catch (InterruptedException interEx) {
            LOGGER.error("An interrupted exception occurred in the Event manager - this event processing will not continue.", interEx);
        }
    }
}

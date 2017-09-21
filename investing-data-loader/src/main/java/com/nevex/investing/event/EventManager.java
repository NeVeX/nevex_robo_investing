package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public final class EventManager {

    private final BlockingQueue<Event> allEventQueue;
    private final ExecutorService executorService;
    private final static Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
    private final Map<Class<? extends Event>, Set<EventConsumer<? extends Event>>> eventConsumers = new ConcurrentHashMap<>();

    public EventManager(Set<EventConsumer<? extends Event>> consumers, int queueSize) {

        this.allEventQueue = new ArrayBlockingQueue<>(queueSize);
        this.executorService = Executors.newSingleThreadExecutor(); // for now...

        setupTheEventConsumerMappings(consumers);
        printEventConsumerMappings();
        startTheQueueProcessor();
    }

    private void startTheQueueProcessor() {
        if ( eventConsumers.isEmpty() ) {
            LOGGER.warn("Not starting the Event Manager queue analyzer since there are no event consumers setup");
            return;
        }
        executorService.submit(this::continuallyProcessEventQueue);
        executorService.shutdown();
        LOGGER.info("The Event Manager queue analyzer has been started - will accept events");
    }

    private void continuallyProcessEventQueue() {
        try {
            while ( !Thread.currentThread().isInterrupted()) {
                final Event event = allEventQueue.take();
                // let's send the event to all of it's consumers
                Set<EventConsumer<? extends Event>> consumers = eventConsumers.get(event.getClass());
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

    public boolean sendEvent(Event event) {
        if ( !isThereAConsumerForEvent(event)) {
            LOGGER.warn("Cannot processed event of type [{}] since there is no registered consumers for it", event.getClass());
            return false;
        }
        allEventQueue.add(event);
        return true;
    }

    @PreDestroy
    void preDestroy() {
        if ( executorService != null ) {
            executorService.shutdownNow();
        }
        LOGGER.warn("Shutting down the event manager");
    }

    private boolean isThereAConsumerForEvent(Event event) {
        // no event consumers to process this event
        return !eventConsumers.isEmpty() && eventConsumers.containsKey(event.getClass());
    }

    private void setupTheEventConsumerMappings(Set<EventConsumer<? extends Event>> consumers) {
        for ( EventConsumer<? extends Event> consumer : consumers) {
            // check what event it supports
            Class<? extends Event> supportedEvent = consumer.getSupportedEventType();

            Set<EventConsumer<? extends Event>> thisEventConsumers = new HashSet<>();
            if ( eventConsumers.containsKey(supportedEvent)) {
                thisEventConsumers = eventConsumers.get(supportedEvent);
            } else {
                this.eventConsumers.put(supportedEvent, thisEventConsumers); // add the new entry, since it didn't exist be before
            }
            thisEventConsumers.add(consumer);
        }
    }

    // print out what we have
    private void printEventConsumerMappings() {
        StringBuilder sb;
        if ( eventConsumers.isEmpty()) {
            sb = new StringBuilder("\n\n** There are no event mappings to event consumers - events cannot be processed **\n\n");
        } else {
            sb = new StringBuilder("\n\nThe mapping of event types to consumers is as follows.\n")
                    .append("There are [").append(eventConsumers.keySet().size()).append("] events types registered to be consumed:\n");
            eventConsumers.forEach(
                    (k, v) -> {
                        sb.append("    ").append(k.getName()).append(":\n        ");
                        v.stream().forEach( eventCon -> sb.append(eventCon).append(" - [").append(eventCon.getConsumerName()).append("]").append("\n        "));
                        sb.append("\n");
                    }
            );
        }
        LOGGER.info(sb.append("\n\n").toString());
    }
}

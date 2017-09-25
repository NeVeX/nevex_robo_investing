package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public final class EventManager {

    private static EventManager INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
    private final Map<Class, EventQueue> eventTypeToConsumers = new ConcurrentHashMap<>();

    public EventManager(Set<EventConsumer> consumers, int queueSize) {
        synchronized (EventManager.class) {
            if ( INSTANCE != null ) {
                throw new IllegalStateException("There is already an instance of ["+this.getClass()+"] - cannot create another");
            }
            INSTANCE = this;
        }
        Map<Class, TreeSet<EventConsumer>> mappings = getEventTypeToConsumerMappings(consumers);
        for ( Map.Entry<Class, TreeSet<EventConsumer>> entry : mappings.entrySet()) {
            EventQueue eventQueue = new EventQueue(queueSize, entry.getKey(), entry.getValue());
            eventTypeToConsumers.put(entry.getKey(), eventQueue);
        }
        printEventConsumerMappings();
    }

    public static boolean sendEvent(Event event) {
        return INSTANCE.doSendEvent(event);
    }

    private boolean doSendEvent(Event event) {

        EventQueue queue = eventTypeToConsumers.get(event.getClass());

        if ( queue == null) {
            LOGGER.warn("Cannot processed event of type [{}] since there is no registered queue/consumers for it", event.getClass());
            return false;
        }
        try {
            queue.addEvent(event);
            return true;
        } catch (Exception e) {
            LOGGER.error("Could not add event [{}] onto the queue - will return from this attempt", event, e);
            return false;
        }
    }

    @PreDestroy
    void preDestroy() {
        eventTypeToConsumers.values().stream().forEach(EventQueue::destroy);
        LOGGER.warn("Shut down the event manager all of the [{}] queues", eventTypeToConsumers.size());
    }

    private Map<Class, TreeSet<EventConsumer>> getEventTypeToConsumerMappings(Set<EventConsumer> consumers) {
        Map<Class, TreeSet<EventConsumer>> mapping = new HashMap<>();
        for ( EventConsumer consumer : consumers) {
            // check what event it supports
            Class supportedEvent = consumer.getSupportedEventType();

            TreeSet<EventConsumer> thisEventConsumers = new TreeSet<>();
            if ( mapping.containsKey(supportedEvent)) {
                thisEventConsumers = mapping.get(supportedEvent);
            } else {
                mapping.put(supportedEvent, thisEventConsumers); // add the new entry, since it didn't exist be before
            }
            thisEventConsumers.add(consumer);
        }
        return mapping;
    }

    // print out what we have
    private void printEventConsumerMappings() {
        StringBuilder sb;
        if ( eventTypeToConsumers.isEmpty()) {
            sb = new StringBuilder("\n\n** There are no event mappings to event consumers - events cannot be processed **\n\n");
        } else {
            sb = new StringBuilder("\n\nThe mapping of event types to consumers is as follows.\n")
                    .append("There are [").append(eventTypeToConsumers.keySet().size()).append("] events types registered to be consumed:\n");
            eventTypeToConsumers.forEach(
                    (k, v) -> {
                        sb.append("    ").append(k.getName()).append(":\n        ");
                        v.getConsumers().stream().forEach( eventCon -> sb.append(eventCon).append(" - [").append(eventCon.getConsumerName()).append("]").append("\n        "));
                        sb.append("\n");
                    }
            );
        }
        LOGGER.info(sb.append("\n\n").toString());
    }

}

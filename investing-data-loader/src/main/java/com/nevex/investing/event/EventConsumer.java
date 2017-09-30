package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public abstract class EventConsumer<T extends Event> implements Consumer<Object>, Comparable<EventConsumer> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private final AtomicLong eventCounter = new AtomicLong();
    private final Class<T> supportedEventType;

    public abstract int getOrder();
    public abstract String getConsumerName();
    protected abstract void onEvent(T event);

    protected EventConsumer(Class<T> supportedEventType) {
        if ( supportedEventType == null) { throw new IllegalArgumentException("Provided supportedEventType is null"); }
        this.supportedEventType = supportedEventType;
    }

    public Class<T> getSupportedEventType() {
        return supportedEventType;
    }

    public final void accept(Object eventRaw) {
        if ( eventRaw.getClass().isAssignableFrom(supportedEventType)) {
            long counter = eventCounter.incrementAndGet();
            if ( counter % 50 == 0) {
                LOGGER.info("{} has processed {} events", getConsumerName(), counter);
            }
            onEvent(supportedEventType.cast(eventRaw));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventConsumer<?> consumer = (EventConsumer<?>) o;
        return Objects.equals(supportedEventType, consumer.supportedEventType)
                && Objects.equals(getConsumerName(), consumer.getConsumerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(supportedEventType, getConsumerName());
    }

    @Override
    public final int compareTo(EventConsumer other) {
        int comparison = Integer.compare(getOrder(), other.getOrder());
        if (comparison == 0 ) {
            return getConsumerName().compareTo(other.getConsumerName());
        }
        return comparison;
    }
}

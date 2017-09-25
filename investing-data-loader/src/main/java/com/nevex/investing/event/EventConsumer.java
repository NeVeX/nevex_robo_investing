package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public abstract class EventConsumer<T extends Event> implements Consumer<Object>, Comparable<EventConsumer> {

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

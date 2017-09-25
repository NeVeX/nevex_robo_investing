package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public class EventManagerTest {

    @Test
    public void makeSureMappingsAreCorrectForVariousEventTypes() {
        Set<EventConsumer> consumers = new HashSet<>();
        EventOneConsumer eventOneConsumer = new EventOneConsumer();
        EventTwoConsumer eventTwoConsumer = new EventTwoConsumer();
        EventThreeConsumer eventThreeConsumer = new EventThreeConsumer();
        consumers.add(eventOneConsumer);
        consumers.add(eventTwoConsumer);
        consumers.add(eventThreeConsumer);
        EventManager eventManager = new EventManager(consumers, 100);

        // Sent an event for EventOne - it should get consumed by both consumer one and three
        assertThat(eventOneConsumer.getCounter()).isEqualTo(0);
        assertThat(eventTwoConsumer.getCounter()).isEqualTo(0);
        assertThat(eventThreeConsumer.getCounter()).isEqualTo(0);

        // send an event
        EventManager.sendEvent(new EventOne());
        sleepForSeconds(1); // allow it to process the event

        assertThat(eventOneConsumer.getCounter()).isEqualTo(1); // should of gotten updated
        assertThat(eventTwoConsumer.getCounter()).isEqualTo(0);
        assertThat(eventThreeConsumer.getCounter()).isEqualTo(1); // should of gotten updated

        EventManager.sendEvent(new EventTwo());
        EventManager.sendEvent(new EventTwo());
        EventManager.sendEvent(new EventTwo());

        sleepForSeconds(1); // allow it to process the event

        assertThat(eventOneConsumer.getCounter()).isEqualTo(1);
        assertThat(eventTwoConsumer.getCounter()).isEqualTo(3);  // should of gotten updated 3 times
        assertThat(eventThreeConsumer.getCounter()).isEqualTo(1);
    }

    private void sleepForSeconds(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (Exception e) {
            fail("Could not sleep thread for ["+seconds+"] seconds", e);
        }
    }

    private static class EventOne implements Event {}

    private static class EventTwo implements Event {}

    private abstract static class EventCounterConsumer<T extends Event> extends EventConsumer<T> {
        private int counter = 0;
        private EventCounterConsumer(Class<T> eventType) {
            super(eventType);
        }

        @Override
        public void onEvent(T event) {
            counter++;
        }


        @Override
        public int getOrder() {
            return 10;
        }


        int getCounter() { return counter; }
    }

    private static class EventOneConsumer extends EventCounterConsumer<EventOne> {

        private EventOneConsumer() {
            super(EventOne.class);
        }

        @Override
        public String getConsumerName() {
            return "event-one-consumer";
        }
    }

    private static class EventTwoConsumer extends EventCounterConsumer<EventTwo> {

        private EventTwoConsumer() {
            super(EventTwo.class);
        }

        @Override
        public String getConsumerName() {
            return "event-two-consumer";
        }
    }

    // This will consume event one
    private static class EventThreeConsumer extends EventCounterConsumer<EventOne> {

        private EventThreeConsumer() {
            super(EventOne.class);
        }

        @Override
        public String getConsumerName() {
            return "event-three-consumer";
        }
    }

}

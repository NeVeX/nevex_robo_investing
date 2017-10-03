package com.nevex.investing.event;

import com.nevex.investing.event.type.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * Created by Mark Cunningham on 9/24/2017.
 */
class EventQueue {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventQueue.class);
    private final Map<Integer, BlockingQueue<Event>> shardQueues;
    private final int shardAmount;
    private final ExecutorService executorService;
    private final TreeSet<EventConsumer> consumers;
    private final Class<Event> eventType;

    public EventQueue(int queueSize, int shardAmount, Class<Event> eventType, TreeSet<EventConsumer> consumers) {
        this.shardQueues = new ConcurrentHashMap<>();
        IntStream.range(0, shardAmount).forEach(i -> this.shardQueues.put(i, new ArrayBlockingQueue<>(queueSize)));
        this.executorService = Executors.newFixedThreadPool(shardAmount);
        this.consumers = consumers;
        this.eventType = eventType;
        this.shardAmount = shardAmount;
        init();
    }

    private void init() {
        if ( consumers.isEmpty() ) {
            LOGGER.warn("Not starting the queue for event type [{}] since there are no event consumers setup", eventType);
            return;
        }

        // Add a thread for each shardAmount queue we want
        IntStream.range(0, shardAmount).forEach(i -> executorService.submit(() -> this.continuallyProcessEventQueue(i)));

        executorService.shutdown();
        LOGGER.info("The event queue has been [{}] started with [{}] consumers - will accept events", eventType, consumers.size());
    }

    private int getShardForNumber(int number) {
        return number % shardAmount;
    }

    void addEvent(Event event) throws Exception {
        int shard = getShardForNumber(event.getId());
        shardQueues.get(shard).put(event);
    }

    void destroy() {
        if ( executorService != null ) {
            executorService.shutdownNow();
        }
    }

    Set<EventConsumer> getConsumers() { return consumers; }

    private void continuallyProcessEventQueue(int shardQueue) {
        try {
            final AtomicLong eventCounter = new AtomicLong();
            BlockingQueue<Event> queue = shardQueues.get(shardQueue);
            while ( !Thread.currentThread().isInterrupted()) {

                final Event event = queue.take();

                long counter = eventCounter.incrementAndGet();

                // let's send the event to all of it's consumers
                LOGGER.debug("Dequeue'd event [{}] and will now invoke all it's [{}] consumers", event, consumers.size());
                consumers.stream().forEach(consumer -> {
                    try {
                        consumer.accept(event);
                        if ( counter % 50 == 0) {
                            LOGGER.info("Shard Queue [{}] for [{}] has processed [{}] events. Current Queue Size: [{}]", shardQueue, consumer.getConsumerName(), counter, queue.size());
                        }

                    } catch (Exception e) {
                        LOGGER.error("Exception raised while processing event [{}] in consumer [{}] - will ignore and continue.", event, consumer.getConsumerName(), e);
                    }
                });
            }
            LOGGER.warn("The event queue will not process any more data since the thread was interrupted for shardAmount [{}]", shardQueue);
        } catch (InterruptedException interEx) {
            LOGGER.error("An interrupted exception occurred in the Event queue for shardAmount [{}] - this event processing will not continue.", shardQueue, interEx);
        }
    }
}

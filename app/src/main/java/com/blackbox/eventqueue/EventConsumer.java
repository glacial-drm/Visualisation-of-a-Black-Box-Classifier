package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * One half of an event queue.
 *
 * This side only allows you to receive events sent by the linked producer.
 */
public class EventConsumer<T> {
    private final ConcurrentLinkedQueue<T> queue;

    protected EventConsumer(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Check whether the event queue is empty.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Receive an event from the producer.
     */
    public T poll() {
        return queue.poll();
    }
}

package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * One half of an event queue.
 *
 * This side only allows you to send events to the linked consumer.
 */
public class EventProducer<T> {
    private final ConcurrentLinkedQueue<T> queue;

    protected EventProducer(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Send an event to the consumer.
     */
    public void send(T event) {
        if (event == null) return;

        queue.offer(event);
    }
}

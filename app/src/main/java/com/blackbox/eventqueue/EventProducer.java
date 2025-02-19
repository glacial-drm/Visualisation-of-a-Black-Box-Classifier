package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventProducer<T> {
    private final ConcurrentLinkedQueue<T> queue;

    protected EventProducer(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    public void send(T event) {
        if (event == null) return;

        queue.offer(event);
    }
}

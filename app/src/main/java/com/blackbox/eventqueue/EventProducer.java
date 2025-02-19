package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventProducer<T> {
    private ConcurrentLinkedQueue<T> queue;

    protected EventProducer(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    public void send(T event) {
        queue.offer(event);
    }
}

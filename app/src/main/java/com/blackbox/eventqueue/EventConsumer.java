package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventConsumer<T> {
    private ConcurrentLinkedQueue<T> queue;

    protected EventConsumer(ConcurrentLinkedQueue<T> queue) {
        this.queue = queue;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public T poll() {
        return queue.poll();
    }
}

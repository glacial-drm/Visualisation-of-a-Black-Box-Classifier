package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventQueue {
    public static <T> ProducerConsumerPair<T> createPair() {
        ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

        return new ProducerConsumerPair<>(new EventProducer<>(queue), new EventConsumer<>(queue));
    }
}

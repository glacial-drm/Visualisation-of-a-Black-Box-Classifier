package com.blackbox.eventqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creates a 1 directional event queue with a producer and consumer.
 *
 * This means unless you have both the producer and the consumer,
 * you can only either send or receive events.
 *
 * This eliminates any confusion over which side of an event queue you are on,
 * by encoding it in the type system, and removing the unused methods.
 */
public class ProducerConsumerPair<T> {
    public final EventProducer<T> producer;
    public final EventConsumer<T> consumer;

    protected ProducerConsumerPair(EventProducer<T> producer, EventConsumer<T> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public ProducerConsumerPair() {
        ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

        producer = new EventProducer<>(queue);
        consumer = new EventConsumer<>(queue);
    }
}

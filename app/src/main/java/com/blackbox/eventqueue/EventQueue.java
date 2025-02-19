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
public class EventQueue {
    public static <T> ProducerConsumerPair<T> createPair() {
        ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

        return new ProducerConsumerPair<>(new EventProducer<>(queue), new EventConsumer<>(queue));
    }
}

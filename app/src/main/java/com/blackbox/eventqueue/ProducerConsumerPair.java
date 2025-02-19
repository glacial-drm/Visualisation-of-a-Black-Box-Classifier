package com.blackbox.eventqueue;

public class ProducerConsumerPair<T> {
    public final EventProducer<T> producer;
    public final EventConsumer<T> consumer;

    protected ProducerConsumerPair(EventProducer<T> producer, EventConsumer<T> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }
}

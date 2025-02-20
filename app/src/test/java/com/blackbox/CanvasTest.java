package com.blackbox;

import com.blackbox.eventqueue.EventConsumer;
import com.blackbox.eventqueue.EventProducer;
import com.blackbox.eventqueue.ProducerConsumerPair;
import com.blackbox.graph.Canvas;
import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.PointsLoaded;
import com.blackbox.graph.events.StopEvent;
import com.blackbox.model.PointData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CanvasTest {
    ProducerConsumerPair<CanvasEvent> eventQueuePair;
    EventProducer<CanvasEvent> eventProducer;
    EventConsumer<CanvasEvent> eventConsumer;

    @BeforeEach
    void init() {
        eventQueuePair = new ProducerConsumerPair<>();
        eventProducer = eventQueuePair.producer;
        eventConsumer = eventQueuePair.consumer;
    }

    @AfterEach
    void cleanup() {
        eventQueuePair = null;
        eventProducer = null;
        eventConsumer = null;
    }

    /**
     * Test the Canvas starting and stopping.
     * Including the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
     * The real test here is in init and cleanup.
     */
    @Test
    void testStartStop() {
        eventProducer.send(new StopEvent(false));
        Canvas.run(eventConsumer, null);
        assertTrue(true);
    }

    /**
     * Test the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
     * This can't test any output, just tests it doesn't crash.
     */
    @Test
    void testPointsLoaded() {
        eventProducer.send(new PointsLoaded(new PointData(0f, 0f, 0f, 0), false));
        eventProducer.send(new StopEvent(false));
        Canvas.run(eventConsumer, null);
        assertTrue(true);
    }
}

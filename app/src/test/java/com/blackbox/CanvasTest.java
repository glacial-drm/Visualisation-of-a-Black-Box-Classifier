package com.blackbox;

import com.blackbox.app.events.ControllerEvent;
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
    ProducerConsumerPair<ControllerEvent> controllerEventQueuePair;

    EventProducer<CanvasEvent> canvasEventProducer;
    EventConsumer<CanvasEvent> canvasEventConsumer;

    EventProducer<ControllerEvent> controllerEventProducer;
    EventConsumer<ControllerEvent> controllerEventConsumer;


    @BeforeEach
    void init() {
        eventQueuePair = new ProducerConsumerPair<>();
        controllerEventQueuePair = new ProducerConsumerPair<>();

        canvasEventProducer = eventQueuePair.producer;
        canvasEventConsumer = eventQueuePair.consumer;

        controllerEventProducer = controllerEventQueuePair.producer;
        controllerEventConsumer = controllerEventQueuePair.consumer;
    }

    @AfterEach
    void cleanup() {
        eventQueuePair = null;
        canvasEventProducer = null;
        canvasEventConsumer = null;
    }

    /**
     * Test the Canvas starting and stopping.
     * Including the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
     * The real test here is in init and cleanup.
     */
    @Test
    void testStartStop() {
        canvasEventProducer.send(new StopEvent(false));
        Canvas.run(canvasEventConsumer, controllerEventProducer);
        assertTrue(true);
    }

    /**
     * Test the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
     * This can't test any output, just tests it doesn't crash.
     */
    @Test
    void testPointsLoaded() {
        canvasEventProducer.send(new PointsLoaded(new PointData(new float[] { 0f, 0f, 0f }, 0), false));
        canvasEventProducer.send(new StopEvent(false));
        Canvas.run(canvasEventConsumer, controllerEventProducer);
        assertTrue(true);
    }
}

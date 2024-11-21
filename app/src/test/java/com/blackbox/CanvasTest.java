package com.blackbox;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blackbox.graph.Canvas;
import com.blackbox.graph.events.*;

public class CanvasTest {
    ConcurrentLinkedQueue<CanvasEvent> eventQueue;

    @BeforeEach
    void init() {
        eventQueue = new ConcurrentLinkedQueue<>();
    }

    @AfterEach
    void cleanup() {
        eventQueue = null;
    }

    /**
     * Test the Canvas starting and stopping.
     * Including the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
     * The real test here is in init and cleanup.
     */
    @Test
    void testStartStop() {
        eventQueue.add(new StopEvent(false));
        Canvas.run(eventQueue);
        assertTrue(true);
    }

    /**
     * Test the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
     * This can't test any output, just tests it doesn't crash.
     */
    @Test
    void testPointsLoaded() {
        eventQueue.add(new PointsLoaded(1));
        eventQueue.add(new StopEvent(false));
        Canvas.run(eventQueue);
        assertTrue(true);
    }
}

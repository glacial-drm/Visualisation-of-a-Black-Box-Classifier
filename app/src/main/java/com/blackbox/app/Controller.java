package com.blackbox.app;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.PointsLoaded;

public class Controller {
    /** The model. */
    private Model model;

    /**
     * A message queue going to the {@link com.blackbox.graph.Canvas Canvas}.
     *
     * Messages passed here will be processed in the canvas render loop.
     */
    public ConcurrentLinkedQueue<CanvasEvent> eventQueue = new ConcurrentLinkedQueue<>();

    /**
     * Send an event to the {@link com.blackbox.graph.Canvas Canvas}.
     *
     * @param event The event to send
     */
    private void sendEvent(CanvasEvent event) {
        eventQueue.add(event);
    }

    /**
     * Send the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
     *
     * @param data For now is just an example, will be points in the future
     */
    public void pointsLoaded(int data) {
        sendEvent(new PointsLoaded(data));
    }

    Controller(Model model) {
        this.model = model;
    }

    /** Get the message. */
    public String getMesssge() {
        return model.message;
    }
}

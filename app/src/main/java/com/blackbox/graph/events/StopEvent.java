package com.blackbox.graph.events;

/**
 * Stop the canvas thread.
 */
public class StopEvent extends CanvasEvent {
    /**
     * Whether all threads should be quit on receiving the event. <br>
     * This is useful right now for testing,
     * and may be used in the future when windows can be open independently.
     */
    public boolean quitAllThreads;

    public StopEvent(boolean quitAllThreads) {
        this.quitAllThreads = quitAllThreads;
    }
}

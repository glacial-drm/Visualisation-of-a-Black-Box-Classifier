package com.blackbox.graph.events;

/**
 * For when the points have been loaded,
 * and now need to be passed to the renderer.
 */
public class PointsLoaded extends CanvasEvent {
    /** Some example data */
    public final int data;

    public PointsLoaded(final int data) {
        this.data = data;
    }
}

package com.blackbox.graph.events;

/**
 * For when the dimension changes, e.g. from 2D to 3D.
 * This is based on which training data generator is selected.
 */
public class SamplingResolutionChanged extends CanvasEvent {
    public final int resolution;

    public SamplingResolutionChanged(final int resolution) {
        this.resolution = resolution;
    }
}

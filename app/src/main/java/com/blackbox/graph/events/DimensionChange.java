package com.blackbox.graph.events;

/**
 * For when the dimension changes, e.g. from 2D to 3D.
 * This is based on which training data generator is selected.
 */
public class DimensionChange extends CanvasEvent {
    public final int dimension;

    public DimensionChange(final int dimension) {
        this.dimension = dimension;
    }
}

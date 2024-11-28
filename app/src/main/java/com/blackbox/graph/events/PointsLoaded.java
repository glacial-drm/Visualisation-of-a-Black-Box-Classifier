package com.blackbox.graph.events;

import com.blackbox.model.PointData;

/**
 * For when the points have been loaded,
 * and now need to be passed to the renderer.
 */
public class PointsLoaded extends CanvasEvent {
    public final PointData point;

    public PointsLoaded(final PointData point) {
        this.point = point;
    }
}

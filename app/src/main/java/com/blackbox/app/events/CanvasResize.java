package com.blackbox.app.events;

import com.blackbox.model.PointData;

public class CanvasResize extends ControllerEvent {
    public final int width;
    public final int height;

    public CanvasResize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
}

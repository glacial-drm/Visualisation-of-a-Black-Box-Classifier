package com.blackbox.app.events;

public class CenterMoved extends ControllerEvent {
    public final int x;
    public final int y;

    public CenterMoved(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
}

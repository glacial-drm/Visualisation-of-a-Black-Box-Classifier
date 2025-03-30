package com.blackbox.app.events;

public class FovChange extends ControllerEvent {
    public final float fov;

    public FovChange(final float fov) {
        this.fov = fov;
    }
}

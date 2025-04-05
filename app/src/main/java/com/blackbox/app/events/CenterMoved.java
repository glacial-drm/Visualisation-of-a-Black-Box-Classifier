package com.blackbox.app.events;

import org.joml.Vector3f;

public class CenterMoved extends ControllerEvent {
    public final Vector3f position;

    public CenterMoved(final Vector3f position) {
        this.position = position;
    }
}

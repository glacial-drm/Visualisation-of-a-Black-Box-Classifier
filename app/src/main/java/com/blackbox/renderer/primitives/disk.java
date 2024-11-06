package com.blackbox.renderer.primitives;

import org.joml.Vector3f;

import java.awt.*;

public class disk extends primitive {
    private float radius;

    /**
     * Constructor for the disk class and sets the object to be a disk. <br>
     * A disk is just a filled in circle given its proper name
     */
    public disk() {
        this.type = PrimitiveTypes.disk;
    }

    public void create(Vector3f pos, float radius, Vector3f rot, Vector3f scale, Color color) {
        this.position = pos;
        this.radius = radius;
        this.rotation = rot;
        this.scale = scale;
        this.color = color;
    }
}

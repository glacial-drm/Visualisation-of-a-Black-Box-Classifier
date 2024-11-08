package com.blackbox.renderer.primitives;

import org.joml.Vector3f;

import java.awt.*;

/** A base class which handles the underlying logic of objects that are rendered stores info
 * on position, rotation, scale and vertex data */
public class Primitive {
    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;
    protected float[] vertexData;
    protected int[] indices;
    protected Color color;
    protected PrimitiveTypes type;
    protected int dimension;

    void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    void rotate(float dx, float dy, float dz) {
        rotation.add(dx, dy, dz);
    }

    void scale(float dx, float dy, float dz) {
        scale.add(dx, dy, dz);
    }

    PrimitiveTypes getType() {
        return type;
    }
}

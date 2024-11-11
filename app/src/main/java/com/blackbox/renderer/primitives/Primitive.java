package com.blackbox.renderer.primitives;

import javafx.scene.paint.Color;
import org.joml.Vector3f;

/**
 * A base class which handles the underlying logic of objects that are rendered stores info
 * on position, rotation, scale and vertex data
 */
public abstract class Primitive {
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    protected float[] vertexData;
    protected int[] indices;
    protected Color color;
    protected int dimension;

    /** Getter for the position */
    public Vector3f getPosition() {
        return position;
    }
    /** Getter the rotation */
    public Vector3f getRotation() {
        return rotation;
    }

    /** Getter for the scale */
    public float getScale() {
        return scale;
    }

    /** Getter for the indices */
    public int[] getIndices() {
        return indices;
    }

    /** getter for the dimension */
    public int getDimension() {
        return dimension;
    }

    /** moves the object */
    void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    /** rotates the object */
    void rotate(float dx, float dy, float dz) {
        rotation.add(dx, dy, dz);
    }

    /** scales the object */
    void scale(float inc) {
        scale += inc;
    }
}

package com.blackbox.renderer.model.primitives;

import com.blackbox.renderer.controlers.Camera;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Point {
    private final Vector3f position;
    private final Vector3f rotation;
    private final float scale;
    private final Vector3f colour;

    public Point(Vector3f position, Vector3f rotation, float scale, Vector3f colour) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getColour() {
        return colour;
    }

    /**
     * Returns the model transformation matrix.
     */
    public Matrix4f getModelMatrix() {
        return Camera.createTransformMatrix(position, rotation, scale);
    }
}

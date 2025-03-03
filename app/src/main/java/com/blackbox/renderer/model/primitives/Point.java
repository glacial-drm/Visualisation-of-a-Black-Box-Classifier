package com.blackbox.renderer.model.primitives;

import com.blackbox.renderer.controlers.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public record Point(Vector3f position, Vector3f rotation, float scale,
                    Vector3f colour) {

    /**
     * Returns the model transformation matrix.
     */
    public Matrix4f getModelMatrix() {
        return Camera.createTransformMatrix(position, scale);
    }
}

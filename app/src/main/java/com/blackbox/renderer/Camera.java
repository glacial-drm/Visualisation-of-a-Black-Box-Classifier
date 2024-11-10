package com.blackbox.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Vector3f cameraPos;
    private Vector3f cameraTarget;
    private Vector3f cameraDirection;
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    public Camera(float width, float height, float fov, float near, float far) {
        projectionMatrix = createProjectionMatrix(width, height, fov, near, far);
        viewMatrix = new Matrix4f();
    }

    public Matrix4f createTransformMatrix(Vector3f translation, Vector3f rotation, float scale){
        return new Matrix4f().identity()
                .translate(translation)
                .rotate((float)Math.toRadians(rotation.x()), (new Vector3f(1,0,0)))
                .rotate((float)Math.toRadians(rotation.y()), (new Vector3f(0,1,0)))
                .rotate((float)Math.toRadians(rotation.z()), (new Vector3f(0,0,1)))
                .scale(scale);
    }

    public Matrix4f createProjectionMatrix(float width, float height, float fov, float near, float far) {
        float aspectRatio = (float)width / height;
        float yScale =  (float)((1.0f / Math.toRadians(fov / 2.0f))) * aspectRatio;
        float xScale = yScale / aspectRatio;
        float frustum = far - near;

        return new Matrix4f()
                .m00(xScale)
                .m11(yScale)
                .m22(-((far - near) / frustum))
                .m23(-1)
                .m32(-((2 * far * near) / frustum))
                .m33(0);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}

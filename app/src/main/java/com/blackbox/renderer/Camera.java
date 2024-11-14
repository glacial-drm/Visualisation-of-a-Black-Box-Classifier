package com.blackbox.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private final Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Vector3f position =  new Vector3f(0,0,0);;
    private float yaw = 0;
    private float pitch = 0;
    private float roll = 0;

    /**
     * Sets up the projection and view matrix
     * @param width viewport width
     * @param height viewport height
     * @param fov field of view
     * @param near how close objects should be and still be rendered
     * @param far how far objects should be to still be rendered
     */
    public Camera(float width, float height, float fov, float near, float far) {
        projectionMatrix = createProjectionMatrix(width, height, fov, near, far);
        viewMatrix = createViewMatrix();
    }

    /**
     * Creates a transform matrix which handle translation, rotation, and scaling the object
     * @param translation Vector3 of the objects coordinates
     * @param rotation Vector3 of the objects wanted rotation
     * @param scale float for resizing the object
     * @return Matrix4f transformationMatrix
     */
    public static Matrix4f createTransformMatrix(Vector3f translation, Vector3f rotation, float scale){
        return new Matrix4f()
                .translate(translation)
                .rotate((float)Math.toRadians(rotation.x()), new Vector3f(1,0,0))
                .rotate((float)Math.toRadians(rotation.y()), new Vector3f(0,1,0))
                .rotate((float)Math.toRadians(rotation.z()), new Vector3f(0,0,1))
                .scale(scale);
    }

    /**
     * Creates a perspective camera
     * @param width viewport width
     * @param height viewport height
     * @param fov field of view
     * @param near how close objects can be and still be rendered
     * @param far how far objects can be and still be rendered
     * @return a perspective matrix
     */
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

    public Matrix4f createViewMatrix() {
        return new Matrix4f()
                .identity()
                .rotate((float)Math.toRadians(pitch), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(yaw), new Vector3f(0,1,0))
                .translate(position);
    }

    /**
     * getter for the projection matrix
     * @return projectionMatrix
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Getter for the view matrix
     * @return viewMatrix
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void move(int key) {
        if(key == GLFW_KEY_W){
            position.y += 0.02f;
        } else if (key == GLFW_KEY_S) {
            position.y -= 0.02f;
        } else if (key == GLFW_KEY_A) {
            position.x -= 0.02f;
        } else if (key == GLFW_KEY_D) {
            position.x += 0.02f;
        }
        viewMatrix = createViewMatrix();
    }
}

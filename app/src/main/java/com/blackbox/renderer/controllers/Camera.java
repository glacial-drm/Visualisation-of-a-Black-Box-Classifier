package com.blackbox.renderer.controllers;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.graph.Input.MouseInputNames;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position = new Vector3f(0, 0, (float) InputHandler.getZoom());
    private final Vector3f forward = new Vector3f(0, 0, 1);
    private final Vector3f right = new Vector3f(1, 0, 0);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final float moveSpeed = 0.2f;
    private final float fov;
    private final float near;
    private final float far;
    private final float sensitivity;
    private Matrix4f perspectiveMatrix;
    private Matrix4f viewMatrix;

    /**
     * Sets up the projection and view matrix
     *
     * @param width  viewport width
     * @param height viewport height
     * @param fov    field of view
     * @param near   how close objects should be and still be rendered
     * @param far    how far objects should be to still be rendered
     */
    public Camera(int width, int height, float fov, float near, float far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.sensitivity = 0.01f;

        perspectiveMatrix = createPerspectiveProjection(width, height, fov, near, far);

        viewMatrix = createViewMatrix();
    }

    /**
     * Creates a transform matrix which handle translation, rotation, and scaling the object
     *
     * @param translation Vector3 of the objects coordinates
     * @param scale       float for resizing the object
     * @return Matrix4f transformationMatrix
     */
    public static Matrix4f createTransformMatrix(Vector3f translation, float scale) {
        return new Matrix4f()
                .translate(translation)
                .scale(scale);
    }


    public Matrix4f createPerspectiveProjection(int width, int height, float FOV, float near, float far) {
        perspectiveMatrix = new Matrix4f();
        perspectiveMatrix.setPerspective(FOV, (float) width / height, near, far);
        return perspectiveMatrix;
    }

    /**
     * Creates a view matrix based on the cameras position, pitch, yaw and roll
     *
     * @return Matrix4f which is a viewMatrix
     */
    public Matrix4f createViewMatrix() {
        Vector3f target = new Vector3f(position).add(forward);
        return new Matrix4f().lookAt(position, target, new Vector3f(0, 1, 0));
    }

    /**
     * Getter for the view matrix
     *
     * @return viewMatrix
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }


    /**
     * Handles moving the camera by changing the position which effects the
     * view matrix
     */
    public boolean move() {
        boolean moved;

        // Handle 2D vs 3D in future

        // Enable mouse movement
        if (InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)) {
            mouseMovement2D();
            moved = true;
        } else {
            moved = keyboardMovement2D() || mouseZoom2D();
        }

        if (moved) {
            viewMatrix = createViewMatrix();
        }

        return moved;
    }

    private boolean mouseZoom2D() {
        // Zoom
        float zoom = (float) InputHandler.getZoom();
        if (zoom != 0) {
            position.z = zoom;
            return true;
        }
        return false;
    }

    private void mouseMovement2D() {
        // Movement
        float dx = (float) (InputHandler.getOriginalMouseX() - InputHandler.getMouseX());
        float dy = (float) (InputHandler.getOriginalMouseY() - InputHandler.getMouseY());

        InputHandler.setOriginalMouseX(InputHandler.getMouseX());
        InputHandler.setOriginalMouseY(InputHandler.getMouseY());

        position.x -= dx * sensitivity;
        position.y -= dy * sensitivity;
    }

    private boolean keyboardMovement2D() {
        Vector3f movement = new Vector3f();

        // Handle linear movement up-down and side-to-side

        if (InputHandler.getKeyState('W')) {
            movement.add(new Vector3f(up));
        }
        if (InputHandler.getKeyState('S')) {
            movement.sub(new Vector3f(up));
        }
        if (InputHandler.getKeyState('D')) {
            movement.sub(new Vector3f(right));
        }
        if (InputHandler.getKeyState('A')) {
            movement.add(new Vector3f(right));
        }

        if (movement.length() > 0) {
            movement.normalize();
        }
        movement.mul(moveSpeed);

        // Handle zooming forward-backward

        if (InputHandler.getKeyState('Q')) {
            InputHandler.incZoom(-moveSpeed);
            position.z = (float) InputHandler.getZoom();
        }
        if (InputHandler.getKeyState('E')) {
            InputHandler.incZoom(moveSpeed);
            position.z = (float) InputHandler.getZoom();
        }

        position.add(movement);

        return !movement.equals(new Vector3f());
    }

    public void resize(int width, int height) {
        perspectiveMatrix = createPerspectiveProjection(width, height, fov, near, far);
    }

    public Matrix4f getActiveProjection() {
        return perspectiveMatrix;
    }

    public Matrix4f getPerspective() {
        return perspectiveMatrix;
    }
}

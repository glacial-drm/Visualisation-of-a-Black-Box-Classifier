package com.blackbox.renderer.controlers;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.graph.Input.MouseInputNames;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;

public class Camera {
    private final Vector3f position = new Vector3f(0, 0, (float) InputHandler.getZoom());
    private final float yaw = 0;
    private final float pitch = 0;
    private final float roll = 0;
    private final float moveSpeed = 0.2f;
    private final float fov;
    private final float near;
    private final float far;
    private final float sensitivity;
    private Matrix4f orthogonalMatrix;
    private Matrix4f perspectiveMatrix;
    private Matrix4f viewMatrix;
    private boolean viewChanged = false;

    /**
     * Sets up the projection and view matrix
     *
     * @param width  viewport width
     * @param height viewport height
     * @param fov    field of view
     * @param near   how close objects should be and still be rendered
     * @param far    how far objects should be to still be rendered
     */
    public Camera(float width, float height, float fov, float near, float far) {
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.sensitivity = 0.01f;

        orthogonalMatrix = createOrthogonalProjection(width, height, fov, near, far);
        perspectiveMatrix = createPerspectiveProjection(width, height, fov, near, far);

        viewMatrix = createViewMatrix();
    }

    /**
     * Creates a transform matrix which handle translation, rotation, and scaling the object
     *
     * @param translation Vector3 of the objects coordinates
     * @param rotation    Vector3 of the objects wanted rotation
     * @param scale       float for resizing the object
     * @return Matrix4f transformationMatrix
     */
    public static Matrix4f createTransformMatrix(Vector3f translation, Vector3f rotation, float scale) {
        return new Matrix4f().translate(translation).rotate((float) Math.toRadians(rotation.x()), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(rotation.y()), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(rotation.z()), new Vector3f(0, 0, 1)).scale(scale);
    }

    public Matrix4f createPerspectiveProjection(float width, float height, float FOV, float near, float far) {
        perspectiveMatrix = new Matrix4f();
        perspectiveMatrix.setPerspective(FOV, (float) width / height, near, far);
        return perspectiveMatrix;
    }

    /**
     * Creates a perspective camera
     *
     * @param width  viewport width
     * @param height viewport height
     * @param fov    field of view
     * @param near   how close objects can be and still be rendered
     * @param far    how far objects can be and still be rendered
     * @return a perspective matrix
     */
    public Matrix4f createOrthogonalProjection(float width, float height, float fov, float near, float far) {
        float aspectRatio = width / height;
        float yScale = (float) ((1.0f / Math.toRadians(fov / 2.0f))) * aspectRatio;
        float xScale = yScale / aspectRatio;
        float frustum = far - near;

        return new Matrix4f().m00(xScale).m11(yScale).m22(-((far - near) / frustum)).m23(-1).m32(-((2 * far * near) / frustum)).m33(0);
    }

    /**
     * Creates a view matrix based on the cameras position, pitch, yaw and roll
     *
     * @return Matrix4f which is a viewMatrix
     */
    public Matrix4f createViewMatrix() {
        viewChanged = true;
        return new Matrix4f().identity().rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1)).translate(position);
    }

    /**
     * getter for the projection matrix
     *
     * @return projectionMatrix
     */
    public Matrix4f getOrthogonalMatrix() {
        return orthogonalMatrix;
    }

    /**
     * Getter for the view matrix
     *
     * @return viewMatrix
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getPerspectiveMatrix() {
        return perspectiveMatrix;
    }

    /**
     * Handles moving the camera by changing the position which effects the
     * view matrix
     */
    public boolean move() {
        boolean moved;

        // Enable mouse movement
        if (InputHandler.getMouseState(MouseInputNames.LEFT_CLICK)) {
            mouseMovement();
            moved = true;
        } else {
            moved = keyboardMovement() || mouseZoom();
        }

        if (moved) viewMatrix = createViewMatrix();
        return moved;
    }

    private boolean mouseZoom() {
        // Zoom
        float zoom = (float) InputHandler.getZoom();
        if (zoom != 0) {
            position.z = zoom;
            return true;
        }
        return false;
    }

    private void mouseMovement() {
        // Movement
        float dx = (float) (InputHandler.getOriginalMouseX() - InputHandler.getMouseX());
        float dy = (float) (InputHandler.getOriginalMouseY() - InputHandler.getMouseY());

        InputHandler.setOriginalMouseX(InputHandler.getMouseX());
        InputHandler.setOriginalMouseY(InputHandler.getMouseY());

        position.x -= dx * sensitivity;
        position.y += dy * sensitivity;
    }

    private boolean keyboardMovement() {
        boolean moved = false;
        Map<Character, Runnable> actions = Map.of('W', () -> position.y -= moveSpeed, 'S', () -> position.y += moveSpeed, 'A', () -> position.x += moveSpeed, 'D', () -> position.x -= moveSpeed, 'Q', () -> {
            InputHandler.incZoom(-moveSpeed);
            position.z = (float) InputHandler.getZoom();
        }, 'E', () -> {
            InputHandler.incZoom(moveSpeed);
            position.z = (float) InputHandler.getZoom();
        });

        for (Map.Entry<Character, Runnable> action : actions.entrySet()) {
            if (InputHandler.getKeyState(action.getKey())) {
                action.getValue().run();
                moved = true;
            }
        }
        return moved;
    }

    /**
     * Getter for the viewChanged
     */
    public boolean getViewChanged() {
        return viewChanged;
    }

    /**
     * Set viewChanged to false
     */
    public void resetViewChanged() {
        viewChanged = false;
    }

    public void resize(int width, int height) {
        orthogonalMatrix = createOrthogonalProjection(width, height, fov, near, far);
    }
}

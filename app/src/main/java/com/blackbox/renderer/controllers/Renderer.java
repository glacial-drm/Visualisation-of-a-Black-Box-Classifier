package com.blackbox.renderer.controllers;

import com.blackbox.renderer.model.Model;
import com.blackbox.renderer.model.PointManager;
import com.blackbox.renderer.model.primitives.Point;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public class Renderer {
    private final Model model;

    /**
     * Initializes the renderer with the given model, sets the viewport,
     * and uploads the projection and view matrices.
     *
     * @param model The model to render.
     */
    public Renderer(Model model) {
        this.model = model;
        resize(model.getWidth(), model.getHeight());
        uploadProjectionMatrix();
        uploadViewMatrix();
    }

    public void renderSetUp(){
        clearScreen();
    }

    public boolean rendererMove() {
        return move();
    }

    /**
     * The main render loop.
     */
    public void render() {
    }

    public void render(PointManager pointManager) {
        // Get the shader for rendering points.
        Shader shader = model.getShaders().get(Point.class);
        uploadProjectionMatrix();
        uploadViewMatrix();

        shader.bind();

        // Update per-instance MVP matrices for all points.
        pointManager.prepPoints();
        // Render all points
        glDrawElementsInstanced(GL_TRIANGLES, pointManager.getIndicesLength(),
                GL_UNSIGNED_INT, 0, pointManager.getPointCount());
        pointManager.cleanUp();
        shader.unbind();
    }

    /**
     * Uploads the projection matrix to all world-based shaders.
     */
    private void uploadProjectionMatrix() {
        model.getShaders().values().forEach(shader -> {
            shader.bind();
            if (shader.isWorldBased()) {
                shader.loadUniform("projectionMatrix", model.getCamera().getActiveProjection());
            }
            shader.unbind();
        });
    }

    /**
     * Uploads the view matrix to all world-based shaders.
     */
    private void uploadViewMatrix() {
        model.getShaders().values().forEach(shader -> {
            shader.bind();
            if (shader.isWorldBased()) {
                shader.loadUniform("viewMatrix", model.getCamera().getViewMatrix());
            }
            shader.unbind();
        });
    }

    /**
     * Resizes the viewport and updates the camera.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    public void resize(int width, int height) {
        model.updateSize(width, height);
        glViewport(0, 0, width, height);

        Camera camera = model.getCamera();
        if (camera == null) {
            model.setCamera(new Camera(width, height, (float) Math.toRadians(70), 0.0001f, 100));
        } else {
            camera.resize(width, height);
        }
        uploadProjectionMatrix();
    }

    /**
     * Clears the screen using a default background color.
     */
    private void clearScreen() {
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Updates the view matrix if the camera has moved.
     */
    public boolean move() {
        if (model.getCamera().move()) {
            uploadViewMatrix();
            return true;
        }
        return false;
    }

    public Vector3f getPosition() {
        return model.getCamera().getPosition();
    }
}

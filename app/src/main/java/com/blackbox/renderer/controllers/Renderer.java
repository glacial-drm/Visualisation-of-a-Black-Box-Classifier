package com.blackbox.renderer.controllers;

import com.blackbox.renderer.controllers.Camera;
import com.blackbox.renderer.model.Model;
import com.blackbox.renderer.model.PointLayer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector3f;

public class Renderer {
    private final Model model;

    public Renderer(Model model) {
        this.model = model;
        resize(model.getWidth(), model.getHeight());
        uploadProjectionMatrix();
        uploadViewMatrix();
    }

    /**
     * Prepares the screen by clearing it.
     */
    public void renderSetUp(){
        clearScreen();
    }

    public boolean rendererMove() {
        return move();
    }

    /**
     * Main render loop.
     */
    public void render() {

    }

    /**
     * Main render loop.
     */
    public void render(PointLayer layer) {
        // Update matrices each frame
        uploadProjectionMatrix();
        uploadViewMatrix();

        layer.getPointManager().render(layer.getShader());
    }


    /**
     * Uploads the projection matrix to all world-based shaders.
     */
    private void uploadProjectionMatrix() {
        Camera camera = model.getCamera();
        if(camera == null) {
            return;
        }

        model.getSampledLayer().getShader().bind();
        if(model.getSampledLayer().getShader().isWorldBased()) {
            model.getSampledLayer().getShader().loadUniform("projectionMatrix", camera.getActiveProjection());
        }
        model.getSampledLayer().getShader().unbind();

        model.getTrainingLayer().getShader().bind();
        if(model.getTrainingLayer().getShader().isWorldBased()) {
            model.getTrainingLayer().getShader().loadUniform("projectionMatrix", camera.getActiveProjection());
        }
        model.getTrainingLayer().getShader().unbind();
    }

    /**
     * Uploads the view matrix to all world-based shaders.
     */
    private void uploadViewMatrix() {
        Camera camera = model.getCamera();
        if(camera == null) {
            return;
        }

        model.getSampledLayer().getShader().bind();
        if(model.getSampledLayer().getShader().isWorldBased()) {
            model.getSampledLayer().getShader().loadUniform("viewMatrix", camera.getViewMatrix());
        }
        model.getSampledLayer().getShader().unbind();

        model.getTrainingLayer().getShader().bind();
        if(model.getTrainingLayer().getShader().isWorldBased()) {
            model.getTrainingLayer().getShader().loadUniform("viewMatrix", camera.getViewMatrix());
        }
        model.getTrainingLayer().getShader().unbind();
    }

    /**
     * Resizes the viewport and updates the camera.
     */
    public void resize(int width, int height) {
        model.updateSize(width, height);
        glViewport(0, 0, width, height);

        Camera camera = model.getCamera();
        if (camera == null) {
            camera = new Camera(width, height, model.getFov(), 0.0001f, 100);
            model.setCamera(camera);
        } else {
            camera.resize(width, height);
        }
        uploadProjectionMatrix();
    }

    /**
     * Clears the screen.
     */
    private void clearScreen() {
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Checks if the camera has moved and updates the view accordingly.
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

package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controllers.Camera;

public class Model {
    private int sWidth, sHeight;
    private float fov = (float) Math.toRadians(70.0f);
    private Camera camera;

    private final PointLayer sampledLayer;
    private final PointLayer trainingLayer;

    public Model(int width, int height) {
        this.sWidth = width;
        this.sHeight = height;
        float aspectRatio = (float) width / height;

        sampledLayer = new PointLayer(aspectRatio, fov, "vertex.glsl", "square.glsl");
        trainingLayer = new PointLayer(aspectRatio, fov, "vertex.glsl", "fragment.glsl");
    }

    /**
     * Adds a point to the appropriate layer.
     */
    public void addPoint(PointData point, boolean isTraining) {
        if(isTraining) {
            trainingLayer.addPoint(point, true);
        } else {
            sampledLayer.addPoint(point, false);
        }
    }

    /**
     * Updates the width and height and propagates the aspect ratio change.
     */
    public void updateSize(int width, int height) {
        sWidth = width;
        sHeight = height;
        float aspectRatio = (float) width / height;

        sampledLayer.updateAspectRatio(aspectRatio);
        trainingLayer.updateAspectRatio(aspectRatio);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getWidth() {
        return sWidth;
    }

    public int getHeight() {
        return sHeight;
    }

    public float getFov() {
        return fov;
    }

    public PointLayer getSampledLayer() {
        return sampledLayer;
    }

    public PointLayer getTrainingLayer() {
        return trainingLayer;
    }
}

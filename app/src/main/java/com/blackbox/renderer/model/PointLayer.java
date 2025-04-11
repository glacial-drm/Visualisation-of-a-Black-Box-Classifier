package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controllers.Shader;

public class PointLayer {
    private final PointManager pointManager;
    private final Shader shader;

    public PointLayer(float aspectRatio, float fov, String vertexShaderPath, String fragmentShaderPath) {
        pointManager = new PointManager(aspectRatio, fov);
        shader = new Shader(true);
        shader.create(vertexShaderPath, fragmentShaderPath);
    }

    public void addPoint(PointData pointData, boolean isTraining) {
        pointManager.addPoint(pointData, isTraining);
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public Shader getShader() {
        return shader;
    }

    public void updateAspectRatio(float aspectRatio) {
        pointManager.setAspectRatio(aspectRatio);
    }

    public void updateFov(float fov) {
        pointManager.setFov(fov);
    }

    public void clearPoints() {
        pointManager.clearPoints();
    }
}

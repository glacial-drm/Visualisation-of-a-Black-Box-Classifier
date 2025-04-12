package com.blackbox.renderer.model;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.model.PointData;
import com.blackbox.renderer.controllers.Shader;
import com.blackbox.renderer.model.primitives.Point;
import org.joml.Vector3f;

public class PointLayer {
    private final PointManager pointManager;
    private final Shader shader;

    private float fov;
    private float aspectRatio;

    public PointLayer(float aspectRatio, float fov, String vertexShaderPath, String fragmentShaderPath) {
        this.fov = fov;
        this.aspectRatio = aspectRatio;

        pointManager = new PointManager();
        shader = new Shader(true);
        shader.create(vertexShaderPath, fragmentShaderPath);
    }

    public void addPoint(PointData pointData, boolean isTraining, int samplingResolution) {
        // Don't expect any number of dimensions
        Vector3f position = new Vector3f(
                pointData.getValue(0).orElse(0f),
                pointData.getValue(1).orElse(0f),
                pointData.getValue(2).orElse(0f)
        );

        float brightness = isTraining ? 0.5f : 0.0f;
        Vector3f color = pointData.label == 1
                         ? new Vector3f(1.0f, brightness, brightness)
                         : new Vector3f(brightness, brightness, 1.0f);

        float depth = (float) InputHandler.getZoom();

        float visibleHeight = 2.0f * depth * (float) Math.tan(fov / 2.0f);

        float scale;
        if (isTraining) {
            scale = 0.05f;
        } else {
            scale = visibleHeight / (float)samplingResolution;
        }
        pointManager.addPoint(new Point(position, new Vector3f(0, 0, 0), scale, color));
    }

    public PointManager getPointManager() {
        return pointManager;
    }

    public Shader getShader() {
        return shader;
    }

    public void updateAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void updateFov(float fov) {
        this.fov = fov;
    }

    public void clearPoints() {
        pointManager.clearPoints();
    }
}

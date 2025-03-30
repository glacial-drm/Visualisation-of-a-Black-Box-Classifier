package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controllers.Camera;
import com.blackbox.renderer.controllers.Shader;
import com.blackbox.renderer.model.primitives.Point;

import java.util.HashMap;

/**
 * Stores data which the renderer needs
 * and manages which object handles specific tasks.
 */
public class Model {
    private final HashMap<Class<?>, Shader> shaders = new HashMap<>();
    private final PointManager pointManager;
    private int sWidth, sHeight;
    private Camera camera;

    public Model(int width, int height) {
        this.sWidth = width;
        this.sHeight = height;
        pointManager = new PointManager();
        initShaders();
    }

    /**
     * Loads the shader(s) from files and stores them in a hashmap.
     */
    private void initShaders() {
        Shader pointShader = new Shader(true);
        pointShader.create("vertex.glsl", "fragment.glsl");
        shaders.put(Point.class, pointShader);
    }

    public HashMap<Class<?>, Shader> getShaders() {
        return shaders;
    }

    /**
     * Adds a point (color depends on the label).
     */
    public void addPoint(PointData point, boolean isTraining) {
        pointManager.addPoint(point, isTraining, (float) sWidth / sWidth);
    }

    public void updateSize(int width, int height) {
        sWidth = width;
        sHeight = height;
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

    /**
     * Provides access to the point manager.
     */
    public PointManager getPointManager() {
        return pointManager;
    }
}

package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.controlers.Shader;
import com.blackbox.renderer.model.primitives.Point;

import java.util.HashMap;

/**
 * Stores data which the renderer needs
 * and manages which object handles specific tasks.
 */
public class Model {
    private int sWidth, sHeight;
    private Camera camera;
    private final HashMap<Class<?>, Shader> shaders = new HashMap<>();
    private final PointManager pointManager;

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
    public void addPoint(PointData point) {
        pointManager.addPoint(point);
    }

    public void updateSize(int width, int height) {
        sWidth = width;
        sHeight = height;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getWidth() {
        return sWidth;
    }

    public int getHeight() {
        return sHeight;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Prepares point data for rendering by computing and uploading MVP matrices.
     */
    public void prepPoints() {
        pointManager.uploadModel();
        pointManager.uploadColourData();
    }

    /**
     * Provides access to the point manager.
     */
    public PointManager getPointManager() {
        return pointManager;
    }
}

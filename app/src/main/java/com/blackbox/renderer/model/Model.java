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
    private final PointManager sampledPointManager;
    private final PointManager trainingPointManager;

    private int sWidth, sHeight;
    private float fov = (float) Math.toRadians(70.0f);
    private Camera camera;

    public Model(int width, int height) {
        this.sWidth = width;
        this.sHeight = height;
        sampledPointManager = new PointManager((float)width/height, fov);
        trainingPointManager = new PointManager((float)width/height, fov);
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
        if(isTraining) {
            trainingPointManager.addPoint(point, isTraining);
        } else{
            sampledPointManager.addPoint(point, isTraining);
        }
    }

    public void updateSize(int width, int height) {
        sWidth = width;
        sHeight = height;

        trainingPointManager.setAspectRatio((float) width / (float) height);
        sampledPointManager.setAspectRatio((float) width / (float) height);
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
    public PointManager getSampledPointManager() {
        return sampledPointManager;
    }

    public PointManager getTrainingPointManager() {
        return trainingPointManager;
    }

    public float getFov() {
        return fov;
    }
}

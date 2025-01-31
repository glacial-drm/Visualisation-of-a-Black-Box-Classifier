package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.controlers.Shader;
import com.blackbox.renderer.model.primitives.Point;
import com.blackbox.renderer.model.primitives.Primitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores data which the renderer needs
 * Manages which handle specific tasks
 */
public class Model {
    private int sWidth, sHeight;
    private Camera camera;
    private final HashMap<Class<?>, Shader> shaders = new HashMap<Class<?>, Shader>();
    private final ArrayList<Primitive> objects = new ArrayList<Primitive>();

    private final PointManager pointManager;

    public Model(int width, int height) {
        this.sWidth = width;
        this.sHeight = height;
        pointManager = new PointManager();

        initShaders();
    }

    /**
     * Loads in the shaders from their files and stores them in a hashmap with key of
     * {@link Primitive primitve type} and value as the new
     * {@link Shader shader}
     */
    private void initShaders() {
        // Load the disk shader
        Shader diskShader = new Shader(true);
        diskShader.create("vertex.glsl", "fragment.glsl");
        shaders.put(Point.class, diskShader);
    }

    public HashMap<Class<?>, Shader> getShaders() {
        return shaders;
    }

    /**
     * Add a point.
     * Will be coloured either red or blue depending on the label.
     * For now has a fixed radius.
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

    public List<Point> getObjects() {
        return pointManager.getPoints();
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
}

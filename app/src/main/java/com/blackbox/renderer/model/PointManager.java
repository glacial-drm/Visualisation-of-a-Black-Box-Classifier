package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.model.primitives.Point;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles storing all the points
 * Manages VAO's which allows for instance rendering
 * Manages MVP matrix for each point to allow for instancing
 *
 */
public class PointManager {
    private List<Point> points;

    public PointManager() {
        points = new ArrayList<>();
    }

    public void addPoint(PointData point) {
        Vector3f position = new Vector3f(point.feature1, point.feature2, 0);
        Color color = point.label == 1 ? Color.RED : Color.BLUE;
        points.add(new Point(position, 0.5f, new Vector3f(0, 0, 0), 0.1f, color));
    }

    public List<Point> getPoints() {
        return points;
    }

    public void calculatePointsMVP(Camera camera) {
        // Calculate View and Perspective matrix
        Matrix4f vp = new Matrix4f().mul(camera.createViewMatrix(), camera.getProjectionMatrix());

        // loop through all points and calculate their MVP
        // then set it as the attribute
        for(Point point : points) {
            Matrix4f model = Camera.createTransformMatrix(point.getPosition(), point.getRotation(), point.getScale());
            Matrix4f mvp = new Matrix4f().mul(model, vp);
        }
    }

    public void prepColours() {
        for(Point point : points) {
            point.setColour();
        }
    }
}

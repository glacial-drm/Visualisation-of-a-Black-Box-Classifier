package com.blackbox.renderer.primitives;

import org.joml.Vector3f;

import java.awt.*;

/**
 * A base class which handles the underlying logic of objects that are rendered stores info
 * on position, rotation, scale and vertex data
 */
public class Primitive {
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    protected float[] vertexData;
    protected int[] indices;
    protected Color color;
    protected PrimitiveTypes type;
    protected int dimension;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float[] getVertexData() {
        return vertexData;
    }

    public void setVertexData(float[] vertexData) {
        this.vertexData = vertexData;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    void rotate(float dx, float dy, float dz) {
        rotation.add(dx, dy, dz);
    }

    void scale(float inc) {
        scale += inc;
    }

    PrimitiveTypes getType() {
        return type;
    }
}

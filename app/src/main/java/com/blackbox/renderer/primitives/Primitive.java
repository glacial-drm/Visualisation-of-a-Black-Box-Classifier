package com.blackbox.renderer.primitives;

import com.blackbox.renderer.IndexBuffer;
import com.blackbox.renderer.VertexArray;
import com.blackbox.renderer.VertexBuffer;
import javafx.scene.paint.Color;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * A base class which handles the underlying logic of objects that are rendered stores info
 * on position, rotation, scale and vertex data
 */
public abstract class Primitive {
    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    protected float[] vertexData;
    protected int[] indices;
    protected Color color;
    protected int dimension;
    protected ArrayList<Integer> attributes;
    protected PrimitiveTypes type;

    protected VertexArray vertexArray;
    protected VertexBuffer vertexBuffer;
    protected IndexBuffer indexBuffer;

    public Primitive() {
        attributes = new ArrayList<>();
    }

    /**
     * Getter for the position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Getter the rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Getter for the scale
     */
    public float getScale() {
        return scale;
    }

    public void enableAttributes() {
        vertexArray.bind();
        indexBuffer.bind();

        for (int attribute : attributes) {
            vertexArray.enableVertexAttrib(attribute);
        }
    }

    public void disable() {
        vertexArray.disableVertexAttrib();
        vertexArray.unbind();
    }

    public int getIndexCount() {
        return indexBuffer.getCount();
    }

    public PrimitiveTypes getType() {
        return type;
    }

    public Vector3f getColour() {
        return new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
    }

    /**
     * Getter for the indices
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * getter for the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * moves the object
     */
    void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    /**
     * rotates the object
     */
    void rotate(float dx, float dy, float dz) {
        rotation.add(dx, dy, dz);
    }

    /**
     * scales the object
     */
    void scale(float inc) {
        scale += inc;
    }
}

package com.blackbox.renderer.primitives;

import javafx.scene.paint.Color;
import org.joml.Vector3f;

public class Disk extends Primitive {

    private float radius;

    /**
     * Constructor for the disk class and sets the object to be a disk. <br>
     * A disk is just a filled in circle given its proper name
     */
    public Disk() {
        this.dimension = 3;

        setUpVertexes();
        setUpIndices();
    }


    /**
     * Creates a new disk with the given parameters
     * @param pos the center of the disk
     * @param radius the 'size' of the circle
     * @param rot rotation of the disk
     * @param scale multiplier of the size
     * @param color a JavaFx colour of the disk
     */
    public void create(Vector3f pos, float radius, Vector3f rot, float scale, Color color) {
        this.position = pos;
        this.radius = radius;
        this.rotation = rot;
        this.scale = scale;
        this.color = color;
    }

    /**
     * The vertexes of the disk
     */
    private void setUpVertexes() {
        vertexData = new float[]{-0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };
    }

    /**
     * The indices of the disk
     */
    private void setUpIndices() {
        indices = new int[]{0, 1, 3, 3, 1, 2};
    }

    /**
     * Getter for the vertices
     * @return vertexData
     */
    public float[] getVertices() {
        return vertexData;
    }

    /**
     * getter for the indices
     * @return indices
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * getter for the dimension
     * @return dimension
     */
    public int getDimension() {
        return dimension;
    }

    public float getRadius() {
        return radius;
    }
}

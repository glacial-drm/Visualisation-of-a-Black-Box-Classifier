package com.blackbox.renderer.primitives;

import org.joml.Vector3f;

import java.awt.*;

public class Disk extends Primitive {
    private float radius;

    /**
     * Constructor for the disk class and sets the object to be a disk. <br>
     * A disk is just a filled in circle given its proper name
     */
    public Disk() {
        this.type = PrimitiveTypes.disk;
        this.dimension = 3;

        setUpVertexs();
        setUpIndices();
    }

    public void create(Vector3f pos, float radius, Vector3f rot, float scale, Color color) {
        this.position = pos;
        this.radius = radius;
        this.rotation = rot;
        this.scale = scale;
        this.color = color;
    }

    private void setUpVertexs() {
        vertexData = new float[]{-0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };
    }

    private void setUpIndices() {
        indices = new int[]{0, 1, 3, 3, 1, 2};
    }

    public float[] getVertices() {
        return vertexData;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getDimension() {
        return dimension;
    }
}

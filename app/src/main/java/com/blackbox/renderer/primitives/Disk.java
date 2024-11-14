package com.blackbox.renderer.primitives;

import com.blackbox.renderer.IndexBuffer;
import com.blackbox.renderer.VertexArray;
import com.blackbox.renderer.VertexBuffer;
import javafx.scene.paint.Color;
import org.joml.Vector3f;

import java.util.List;

public class Disk extends Primitive {

    private float radius;

    /**
     * Constructor for the disk class and sets the object to be a disk. <br>
     * A disk is just a filled in circle given its proper name
     */
    public Disk(Vector3f pos, float radius, Vector3f rot, float scale, Color color) {
        super(pos, rot, scale, color);
        this.radius = radius;
        this.dimension = 3;
        this.type = PrimitiveTypes.DISK;

        setUpVertexes();
        setUpIndices();
        setUpBuffers();
    }


    /**
     * Creates a new disk with the given parameters
     * @param pos the center of the disk
     * @param radius the 'size' of the circle
     * @param rot rotation of the disk
     * @param scale multiplier of the size
     * @param color a JavaFx colour of the disk
     */
    public void create() {

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

    private void setUpBuffers(){
        vertexArray = new VertexArray();
        vertexArray.bind();

        indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(getIndices());

        vertexBuffer = new VertexBuffer();
        vertexBuffer.storeFloatData(getVertices(), 0, getDimension());
        attributes.add(0);
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

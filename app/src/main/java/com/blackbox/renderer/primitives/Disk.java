package com.blackbox.renderer.primitives;

import com.blackbox.renderer.VertexBuffer;
import javafx.scene.paint.Color;
import org.joml.Vector3f;

public class Disk extends Primitive {

    private final float radius;
    private static final float[] indexData = new float[]{-0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f,};
    private static final int[] indicesData = new int[]{0, 1, 3, 3, 1, 2};

    /**
     * Creates a new disk with the given parameters and sets up the VertexArray, VertexBuffer and IndexBuffer
     *
     * @param pos    the center of the disk
     * @param radius the 'size' of the circle
     * @param rot    rotation of the disk
     * @param scale  multiplier of the size
     * @param color  a JavaFx colour of the disk
     */
    public Disk(Vector3f pos, float radius, Vector3f rot, float scale, Color color) {
        super(pos, rot, scale, color, indexData, indicesData);
        this.radius = radius;
        this.dimension = 3;
        this.type = PrimitiveTypes.DISK;

        setUpVertexBuffer();
    }

    private void setUpVertexBuffer() {
        vertexBuffer = new VertexBuffer();
        vertexBuffer.storeFloatData(getVertices(), 0, getDimension());
        attributes.add(0);
    }


    public float getRadius() {
        return radius;
    }
}

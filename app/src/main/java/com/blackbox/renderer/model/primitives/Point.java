package com.blackbox.renderer.model.primitives;

import com.blackbox.renderer.controlers.*;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class Point {

    private final float radius;
    private static final float[] indexData = new float[]{-0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f,};
    private static final int[] indicesData = new int[]{0, 1, 3, 3, 1, 2};

    protected Vector3f position;
    protected Vector3f rotation;
    protected float scale;
    protected Color colour;
    protected int dimension;
    protected ArrayList<Integer> attributes;
    protected VertexArray vertexArray;
    protected VertexBuffer vertexBuffer;
    private  VertexBuffer mvpBuffer;
    protected IndexBuffer indexBuffer;


    /**
     * Creates a new disk with the given parameters and sets up the VertexArray, VertexBuffer and IndexBuffer
     * @param pos    the center of the disk
     * @param radius the 'size' of the circle
     * @param rot    rotation of the disk
     * @param scale  multiplier of the size
     * @param color  a JavaFx colour of the disk
     */
    public Point(Vector3f pos, float radius, Vector3f rot, float scale, Color color) {
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
        this.colour = color;

        this.radius = radius;
        this.dimension = 3;
        attributes = new ArrayList<>();
        
        initVertexArray();
        setUpVertexBuffer();
        initIndexBuffer();

        mvpBuffer = new VertexBuffer();
    }

    /**
     * Creates the {@link VertexBuffer vertex buffer}
     * stores the vertex data in the vertex buffer
     * and adds the attribute to the attribute list
     */
    protected void setUpVertexBuffer() {
        float[] interleavedData = {
                -0.5f,  0.5f,  0.0f,    (float) colour.getRed(), (float) colour.getGreen(), (float) colour.getBlue(),
                -0.5f, -0.5f,  0.0f,    (float) colour.getRed(), (float) colour.getGreen(), (float) colour.getBlue(),
                0.5f, -0.5f,  0.0f,    (float) colour.getRed(), (float) colour.getGreen(), (float) colour.getBlue(),
                0.5f,  0.5f,  0.0f,    (float) colour.getRed(), (float) colour.getGreen(), (float) colour.getBlue()
        };

        int stride = (3 + 3) * Float.BYTES;

        vertexArray.bind();
        vertexBuffer = new VertexBuffer();
        vertexBuffer.bind();

        // Upload the interleaved data to the GPU
        vertexBuffer.storeFloatData(interleavedData);

        // Configure position attribute (Attribute 0)
        vertexArray.setVertexAttrib(0, 3, GL_FLOAT, false, stride, 0);
        attributes.add(0);

        // Configure color attribute (Attribute 1)
        vertexArray.setVertexAttrib(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        attributes.add(1);

        vertexBuffer.unbind();
        vertexArray.unbind();
    }

    public void uploadUniforms(Camera camera, Shader shader) {
        Matrix4f transformationMatrix = Camera.createTransformMatrix(getPosition(), getRotation(), getScale());
        shader.loadUniform("transformMatrix", transformationMatrix);
    }

    /** Creates and binds {@link VertexArray vertex array */
    public void initVertexArray(){
        vertexArray = new VertexArray();
        vertexArray.bind();
    }

    /** Creates a {@link IndexBuffer indices buffer} ,binds and stores indices */
    public void initIndexBuffer(){
        indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(indicesData);
    }

    /**
     * Gets the number of indices
     * @return int indexBuffer count
     */
    public int getIndexCount() {
        return indexBuffer.getCount();
    }

    /** Gets and converts it to a {@link org.joml.Vector3f vector3f}*/
    public Vector3f getColour() {
        return new Vector3f((float) colour.getRed(), (float) colour.getGreen(), (float) colour.getBlue());
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

    /**
     * Binds the {@link VertexArray vertex array} and
     * {@link VertexArray index buffer}
     * then enables all used attributes
     */
    public void enableAttributes() {
        vertexArray.bind();
        indexBuffer.bind();

        for (int attribute : attributes) {
            vertexArray.enableVertexAttrib(attribute);
        }
    }

    /**
     * Disables the attributes and the {@link VertexArray vertex array}
     */
    public void disable() {
        vertexArray.disableVertexAttrib();
        vertexArray.unbind();
    }

    /** Getter for the radius of the circle */
    public float getRadius() {
        return radius;
    }

    public void uploadMVP(Matrix4f mvp) {
        vertexArray.bind();
        mvpBuffer.bind();
        mvpBuffer.destroy();
        mvpBuffer = new VertexBuffer();
        mvpBuffer.bind();

        float[] mvpFloat = new float[16];
        mvp.get(mvpFloat);

        mvpBuffer.storeFloatData(mvpFloat);
        int stride = 16 * Float.BYTES;

        for(int y = 0; y < 4; y++){
            int offset = y * 4 * Float.BYTES;
            vertexArray.setVertexAttrib(y+2, 4, GL_FLOAT, false, stride, offset);
            attributes.add(y+2);
            glVertexAttribDivisor(y+2, 1);
        }

        mvpBuffer.unbind();
        vertexArray.unbind();
    }
}

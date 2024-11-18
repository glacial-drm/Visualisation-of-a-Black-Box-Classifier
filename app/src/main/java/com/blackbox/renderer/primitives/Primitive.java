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
    protected Color colour;
    protected int dimension;
    protected ArrayList<Integer> attributes;

    protected VertexArray vertexArray;
    protected VertexBuffer vertexBuffer;
    protected IndexBuffer indexBuffer;

    /**
     * Sets up the primitve by setting needed variables and creating
     * a {@link com.blackbox.renderer.VertexArray vertex array} and
     * a {@link com.blackbox.renderer.VertexArray index buffer}
     * for the primitve
     * @param pos position of the primitive in world space
     * @param rot rotation of the primitive
     * @param scale size multiple
     * @param colour JavaFx colour
     * @param VertexData float[] of vertices in 2D or 3D format
     * @param indicesData int[] of indices
     */
    public Primitive(Vector3f pos, Vector3f rot, float scale, Color colour, float[] VertexData, int[] indicesData) {
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
        this.colour = colour;
        this.vertexData = VertexData;
        this.indices = indicesData;
        attributes = new ArrayList<>();

        initVertexArray();
        initIndexBuffer();
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
     * Binds the {@link com.blackbox.renderer.VertexArray vertex array} and
     * {@link com.blackbox.renderer.VertexArray index buffer}
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
     * Disables the attributes and the {@link com.blackbox.renderer.VertexArray vertex array}
     */
    public void disable() {
        vertexArray.disableVertexAttrib();
        vertexArray.unbind();
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
     * Getter for the vertices
     * @return vertexData
     */
    public float[] getVertices() {
        return vertexData;
    }

    /** Creates and binds {@link com.blackbox.renderer.VertexArray vertex array */
     public void initVertexArray(){
        vertexArray = new VertexArray();
        vertexArray.bind();
    }

    /** Creates a {@link com.blackbox.renderer.IndexBuffer indices buffer} ,binds and stores indices */
    public void initIndexBuffer(){
        indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(getIndices());
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

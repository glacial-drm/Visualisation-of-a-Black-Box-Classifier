package com.blackbox.renderer.controllers;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * Represents an OpenGL Vertex Buffer Object (VBO).
 * Used to store vertex data,
 */
public class VertexBuffer {
    private int vboId;

    // Allocation methods

    /**
     * Constructs a new VertexBuffer object and generates a new OpenGL VBO.
     * Ensures that no other VBO is currently bound before creating the new one.
     *
     * @throws RuntimeException if the VBO creation fails (e.g., if OpenGL returns an invalid ID).
     * @throws IllegalStateException if another VBO is currently bound.
     */
    public VertexBuffer() {
        ensureNonBound();
        vboId = glGenBuffers();

        if (vboId < 0) {
            throw new RuntimeException("Failed to create vertex buffer");
        }
    }

    /**
     * Deletes this VertexBuffer object, freeing its associated OpenGL resources.
     * This method does not require the VBO to be bound.
     * Sets vboId to -1
     */
    public void destroy() {
        glDeleteBuffers(vboId);
        vboId = -1;
    }

    // State methods

    /**
     * Binds this VertexBuffer object, making it the active VBO for subsequent OpenGL operations.
     *
     * @throws IllegalStateException if another VBO is currently bound.
     */
    public void bind() {
        ensureNonBound();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
    }

    /**
     * Unbinds the currently bound VertexBuffer object, reverting to the default VBO.
     *
     * @throws IllegalStateException if this VBO is not currently bound.
     */
    public void unbind() {
        ensureBound();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    // Memory storage methods

    /**
     * Stores the given float data in this VertexBuffer and uploads it to the GPU.
     * This method requires that this VBO is currently bound.
     *
     * @param data an array of floating-point values to store in the buffer.
     * @throws IllegalStateException if this VBO is not bound.
     */
    public void storeFloatData(float[] data) {
        ensureBound();
        // Convert the float array to a FloatBuffer
        FloatBuffer buf = addFloatData(data);
        // Upload the data to the currently bound GL_ARRAY_BUFFER
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
    }

    /**
     * Converts a float array into a FloatBuffer for use with OpenGL.
     *
     * @param data an array of floating-point values.
     * @return a FloatBuffer containing the data.
     */
    private FloatBuffer addFloatData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    // -------------------------------------------------------------------------
    // OpenGL State Check Methods
    // -------------------------------------------------------------------------

    /**
     * Ensures that this VertexBuffer object is currently bound.
     *
     * @throws IllegalStateException if this VBO is not bound.
     */
    private void ensureBound() {
        int current = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        if (current != vboId) {
            throw new IllegalStateException("VBO " + vboId + " is not bound. Current bound VBO is " + current);
        }
    }

    /**
     * Ensures that no VertexBuffer object is currently bound.
     *
     * @throws IllegalStateException if another VBO is bound.
     */
    private void ensureNonBound() {
        int current = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        if (current != 0) {
            throw new IllegalStateException("VBO " + current + " is bound. No VBO should be bound");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the OpenGL ID of this VertexBuffer object.
     *
     * @return the VBO ID.
     */
    public int getVboId() {
        return vboId;
    }
}
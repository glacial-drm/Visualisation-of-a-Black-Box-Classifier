package com.blackbox.renderer.controlers;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * Represents an OpenGL Vertex Array Object (VAO).
 * A VAO is used to store vertex attribute configurations and bindings,
 * making it easier to manage vertex data for rendering.
 */
public class VertexArray {
    private final int vaoId;
    private final List<Integer> attributes = new ArrayList<>();

    // Memory allocation functions
    /**
     * Constructs a new VertexArray object and generates a new OpenGL VAO.
     * Ensures that no other VAO is currently bound before creating the new one.
     *
     * @throws RuntimeException if the VAO creation fails (e.g., if OpenGL returns an invalid ID).
     * @throws IllegalStateException if another VAO is currently bound.
     */
    public VertexArray() {
        ensureNonBound();
        vaoId = glGenVertexArrays();
        if (vaoId <= 0) {
            throw new RuntimeException("Failed to create vertex array");
        }
    }

    /**
     * Deletes this VertexArray object, freeing its associated OpenGL resources.
     * This method does not require the VAO to be bound.
     */
    public void delete() {
        glDeleteVertexArrays(vaoId);
    }

    // State management methods
    /**
     * Binds this VertexArray object, making it the active VAO for subsequent OpenGL operations.
     *
     * @throws IllegalStateException if another VAO is currently bound.
     */
    public void bind() {
        ensureNonBound();
        glBindVertexArray(vaoId);
    }

    /**
     * Unbinds the currently bound VertexArray object, reverting to the default VAO (ID 0).
     *
     * @throws IllegalStateException if this VAO is not currently bound.
     */
    public void unbind() {
        ensureBound();
        glBindVertexArray(0);
    }

    // Getters
    /**
     * Returns the OpenGL ID of this VertexArray object.
     *
     * @return the VAO ID.
     */
    public int getVaoId() {
        return vaoId;
    }

    // Check methods
    /**
     * Ensures that this VertexArray object is currently bound.
     *
     * @throws IllegalStateException if this VAO is not bound.
     */
    private void ensureBound() {
        int current = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        if (current != vaoId) {
            throw new IllegalStateException("VertexArray " + vaoId + " is not bound. Current bound VAO is " + current);
        }
    }

    /**
     * Ensures that no VertexArray object is currently bound.
     *
     * @throws IllegalStateException if another VAO is bound.
     */
    private void ensureNonBound() {
        int current = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        if (current != 0) {
            throw new IllegalStateException("VertexArray " + current + " is bound. No VAO should be bound");
        }
    }

    // Attribute methods
    /**
     * Enables all vertex attribute arrays associated with this VAO.
     * This method requires that this VAO is currently bound.
     *
     * @throws IllegalStateException if this VAO is not bound.
     */
    public void enableVertexAttributes() {
        ensureBound();
        for (int attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
    }

    /**
     * Disables all vertex attribute arrays associated with this VAO.
     * This method requires that this VAO is currently bound.
     *
     * @throws IllegalStateException if this VAO is not bound.
     */
    public void disableVertexAttributes() {
        ensureBound();
        for (int attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }
    }

    /**
     * <p>
     * Configures a vertex attribute pointer for this VAO.
     * This method requires that this VAO is currently bound.
     * </p>
     * @param attribute  The index of the vertex attribute to configure.
     * @param size       The number of components per vertex attribute (e.g., 3 for XYZ coordinates).
     * @param type       The data type of each component (e.g., GL_FLOAT).
     * @param normalized Whether fixed-point data should be normalized (true) or used directly (false).
     * @param stride     The byte offset between consecutive vertex attributes.
     * @param offset     The byte offset of the first component in the attribute.
     * @param instanced  Whether this attribute is instanced (true) or per-vertex (false).
     * @throws IllegalStateException if this VAO is not bound.
     */
    public void setVertexAttrib(int attribute, int size, int type, boolean normalized, int stride, int offset, boolean instanced) {
        ensureBound();
        glVertexAttribPointer(attribute, size, type, normalized, stride, offset);
        glEnableVertexAttribArray(attribute);
        if (instanced) {
            glVertexAttribDivisor(attribute, 1);
        }
        attributes.add(attribute);
    }
}
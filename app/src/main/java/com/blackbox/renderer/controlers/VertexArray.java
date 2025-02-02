package com.blackbox.renderer.controlers;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class VertexArray {
    private final int vaoId;
    private final List<Integer> attributes = new ArrayList<>();

    /**
     * Creates a new OpenGL vertex array and stores its ID.
     */
    public VertexArray() {
        vaoId = glGenVertexArrays();
        if (vaoId <= 0) {
            throw new RuntimeException("Failed to create vertex array");
        }
    }

    /**
     * Binds this vertex array.
     */
    public void bind() {
        glBindVertexArray(vaoId);
    }

    /**
     * Unbinds any vertex array.
     */
    public void unbind() {
        glBindVertexArray(0);
    }

    /**
     * Deletes this vertex array from GPU memory.
     */
    public void delete() {
        glDeleteVertexArrays(vaoId);
    }

    /**
     * Returns the ID of this vertex array.
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * Checks that this VAO is currently bound.
     * Throws an IllegalStateException if it is not.
     */
    private void ensureBound() {
        int current = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        if (current != vaoId) {
            throw new IllegalStateException("VertexArray " + vaoId +
                    " is not bound. Current bound VAO is " + current);
        }
    }

    /**
     * Enables all stored vertex attribute arrays.
     * This method requires that this VAO is already bound.
     */
    public void enableVertexAttributes() {
        ensureBound();
        for (int attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
    }

    /**
     * Disables all stored vertex attribute arrays.
     * This method requires that this VAO is already bound.
     */
    public void disableVertexAttributes() {
        ensureBound();
        for (int attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }
    }

    /**
     * Configures a vertex attribute pointer.
     * <p>
     * This method requires that this VAO is already bound.
     * </p>
     *
     * @param attribute   The attribute index.
     * @param size        The number of components per vertex attribute.
     * @param type        The data type (e.g. GL_FLOAT).
     * @param normalized  Whether fixed-point data should be normalized.
     * @param stride      Byte offset between consecutive attributes.
     * @param offset      Byte offset of the first attribute.
     * @param instanced   Whether this attribute is instanced.
     */
    public void setVertexAttrib(int attribute, int size, int type, boolean normalized,
                                int stride, int offset, boolean instanced) {
        ensureBound();
        glVertexAttribPointer(attribute, size, type, normalized, stride, offset);
        glEnableVertexAttribArray(attribute);
        if (instanced) {
            glVertexAttribDivisor(attribute, 1);
        }
        attributes.add(attribute);
    }
}

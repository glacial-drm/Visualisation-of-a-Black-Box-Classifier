package com.blackbox.renderer.controlers;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    private final int vaoId;

    /**
     * Create an openGL vertex array and stores the value in vaoId
     */
    public VertexArray() {
        vaoId = glGenVertexArrays();

        if(vaoId < 0) {
            throw new RuntimeException("Failed to create vertex array");
        }
    }

    /**
     * Enable the given vertex attribute
     *
     * @param attribute the index in the VertexArray
     */
    public void enableVertexAttrib(int attribute) {
        glEnableVertexAttribArray(attribute);
    }

    /**
     * Binds the current vertex array
     */
    public void bind() {
        assert glGetInteger(GL_VERTEX_ARRAY_BINDING) == 0;

        glBindVertexArray(vaoId);
    }

    /**
     * unbinds the current vao
     */
    public void unbind() {
        assert vaoId == glGetInteger(GL_VERTEX_ARRAY_BINDING);

        glBindVertexArray(0);
    }

    /**
     * Clears the memory allocated on the gpu
     */
    public void delete() {
        glDeleteBuffers(vaoId);
    }

    /**
     * Getter for the vaoID
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * disables the shader arg
     */
    public void disableVertexAttrib() {
        glDisableVertexAttribArray(0);
    }

    public void setVertexAttrib(int attribute, int size, int type, boolean normalised, int stride, int offset) {
        glVertexAttribPointer(attribute, size, type, normalised, stride, offset); // Position: 3 floats, starts at offset 0
        glEnableVertexAttribArray(attribute);
    }
}

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
    private List<Integer> attributes;

    /**
     * Create an openGL vertex array and stores the value in vaoId
     */
    public VertexArray() {
        vaoId = glGenVertexArrays();
        attributes = new ArrayList<>();
        if(vaoId < 0) {
            throw new RuntimeException("Failed to create vertex array");
        }
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
     * Enables all attributes
     */
    public void enableVertexAttributes() {
        for(Integer attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
    }

    /**
     * disables the shader arg
     */
    public void disableVertexAttributes() {
        for(Integer attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }
    }


    public void setVertexAttrib(int attribute, int size, int type, boolean normalised,
                                int stride, int offset, boolean instanced) {
        glVertexAttribPointer(attribute, size, type, normalised, stride, offset); // Position: 3 floats, starts at offset 0
        glEnableVertexAttribArray(attribute);

        if(instanced) {
            glVertexAttribDivisor(attribute, 1);
        }

        attributes.add(attribute);
    }
}

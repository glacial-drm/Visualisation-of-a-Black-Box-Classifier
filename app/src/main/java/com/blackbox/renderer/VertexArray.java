package com.blackbox.renderer;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArray {
    private final int vaoId;

    /**
     * Create an openGL vertex array and stores the value in vaoId
     */
    public VertexArray() {
        vaoId = glGenVertexArrays();
    }

    /**
     * Enable the given vertex attribute
     *
     * @param attribute the index in the VertexArray
     * @return true if nothing fails
     */
    public boolean enableVertexAttrib(int attribute) {
        glEnableVertexAttribArray(attribute);
        return true;
    }

    /**
     * Binds the current vertex array
     *
     * @return
     * true if nothing fails
     */
    public boolean bind() {
        glBindVertexArray(vaoId);
        return true;
    }

    /**
     * unbinds the current vao
     *
     * @return true if nothing fails
     */
    public boolean unbind() {
        glBindVertexArray(0);
        return true;
    }

    /**
     * Clears the memory allocated on the gpu
     * @return true if nothing fails
     */
    public boolean delete() {
        glDeleteBuffers(vaoId);
        return true;
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
}

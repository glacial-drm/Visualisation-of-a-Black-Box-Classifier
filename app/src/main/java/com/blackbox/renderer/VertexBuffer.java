package com.blackbox.renderer;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexBuffer {
    private final int vboId;

    /**
     * Creates a buffer stores id in vboId
     */
    public VertexBuffer() {
        vboId = glGenBuffers();
    }


    /**
     * Clears the allocated memory on the GPU
     *
     * @return true if no error
     */
    public boolean destroy() {
        glDeleteBuffers(vboId);
        return true;
    }

    /**
     * Binds the buffer to a GL_ARRAY_BUFFER
     *
     * @return true if nothing fails
     */
    public boolean bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        return true;
    }

    /**
     * Unbinds this vbo
     *
     * @return true if no fail
     */
    public boolean unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return true;
    }

    /**
     * Stores the given data inside the buffer ready to send to the GPU and unbinds the VertexBuffer
     *
     * @param data      array of floating point values
     * @param attribute relates to the shader in
     * @param dimension between 1 and 4 for how the GPU should understand the data
     */
    public void storeFloatData(float[] data, int attribute, int dimension) {
        if ((dimension <= 0) || (dimension > 4)) {
            throw new IllegalArgumentException("dimension must be between 1 and 4");
        }
        if (data.length % dimension != 0 || data.length == 0) {
            throw new IllegalArgumentException("data is not a multiple of dimension");
        }

        bind();
        FloatBuffer buf = addFloatData(data);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        glVertexAttribPointer(attribute, dimension, GL_FLOAT, false, 0, 0);
        unbind();
    }

    /**
     * Preps the FloatBuffer for sending to the GPU
     *
     * @param data floating point values of data
     * @return FloatBuffer of the data
     */
    private FloatBuffer addFloatData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Getter for vboId
     */
    public int getVboId() {
        return vboId;
    }
}

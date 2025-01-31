package com.blackbox.renderer.controlers;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexBuffer {
    private final int vboId;

    /**
     * Creates a buffer stores id in vboId
     */
    public VertexBuffer() {
        vboId = glGenBuffers();

        if (vboId < 0) {
            throw new RuntimeException("Failed to create vertex buffer");
        }
    }


    /**
     * Clears the allocated memory on the GPU
     */
    public void destroy() {
        glDeleteBuffers(vboId);
    }

    /**
     * Binds the buffer to a GL_ARRAY_BUFFER
     */
    public void bind() {
        assert glGetInteger(GL_ARRAY_BUFFER_BINDING) == 0;

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
    }

    /**
     * Unbinds this vbo
     */
    public void unbind() {
        assert vboId == glGetInteger(GL_ARRAY_BUFFER_BINDING);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Stores the given data inside the buffer ready to send to the GPU and unbinds the VertexBuffer
     *
     * @param data      array of floating point values
     */
    public void storeFloatData(float[] data) {
        // Validate and create a FloatBuffer
        FloatBuffer buf = addFloatData(data);
        // Upload the data to the currently bound GL_ARRAY_BUFFER
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
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

package com.blackbox.renderer;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {
    private final int iboId;
    private int count;

    /**
     * Creates a glBuffer and stores its id in iboId
     */
    public IndexBuffer() {
        iboId = glGenBuffers();

        if (iboId < 0) {
            throw new RuntimeException("Unable to create index buffer");
        }
    }

    /**
     * Clears the allocated memory on the GPU
     */
    public void destroy() {
        glDeleteBuffers(iboId);
    }

    /**
     * Binds this Index buffer as a GL_ELEMENT_ARRAY_BUFFER
     */
    public void bind() {
        // Ensure no other buffer is bound
        assert 0 == glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
    }

    /**
     * Unbinds this Index Buffer
     */
    public void unbind() {
        // Ensure this buffer is bound
        assert iboId == glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Preps the data to be sent to the GPU and buffers it
     *
     * @param indices list of indices for the model
     */
    public void storeIndexData(int[] indices) {
        if (indices.length == 0 || indices.length % 3 != 0) {
            throw new IllegalArgumentException("The number of indices must be a multiple of 3 and non 0");
        }

        // This buffer is not currently active
        assert iboId == glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);

        count = indices.length;
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public int getIboId() {
        return iboId;
    }

    public int getCount() {
        return count;
    }
}

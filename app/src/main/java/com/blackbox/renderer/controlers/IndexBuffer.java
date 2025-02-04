package com.blackbox.renderer.controlers;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;


public class IndexBuffer {
    private int iboId;
    private int count;

    /**
     * Creates a new IndexBuffer and generates an OpenGL buffer ID.
     * <p>
     * This constructor ensures that no other index buffer is currently bound before creating
     * this one. If the generated buffer ID is less than 0, a {@link RuntimeException} is thrown.
     * </p>
     *
     * @throws RuntimeException if the index buffer could not be created.
     */
    public IndexBuffer() {
        ensureNonBound();
        iboId = glGenBuffers();

        if (iboId < 0) {
            throw new RuntimeException("Unable to create index buffer");
        }
    }

    /**
     * Deletes the index buffer from the GPU, releasing any allocated resources.
     * <p>
     * This should be called when the index buffer is no longer needed to prevent memory leaks.
     * </p>
     */
    public void destroy() {
        glDeleteBuffers(iboId);
        iboId = -1;
    }

    /**
     * Binds this index buffer as the current {@code GL_ELEMENT_ARRAY_BUFFER}.
     * <p>
     * The method first ensures that no other index buffer is bound.
     * </p>
     *
     * @throws IllegalStateException if another index buffer is already bound.
     */
    public void bind() {
        ensureNonBound();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
    }

    /**
     * Unbinds this index buffer from the {@code GL_ELEMENT_ARRAY_BUFFER} target.
     * <p>
     * It ensures that this index buffer is currently bound before unbinding.
     * </p>
     *
     * @throws IllegalStateException if this index buffer is not currently bound.
     */
    public void unbind() {
        ensureBound();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Uploads index data to the GPU.
     *
     * @param indices an array of indices that reference vertices.
     * @throws IllegalArgumentException if the indices array is empty or its length is not a multiple of 3.
     * @throws IllegalStateException    if the index buffer is not bound when this method is called.
     */
    public void storeIndexData(int[] indices) {
        ensureBound();
        if (indices.length == 0 || indices.length % 3 != 0) {
            throw new IllegalArgumentException("The number of indices must be a multiple of 3 and non 0");
        }

        count = indices.length;
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    // -------------------------------------------------------------------------
    // OpenGL State Check Methods
    // -------------------------------------------------------------------------

    /**
     * Ensures that this index buffer is currently bound.
     * <p>
     * It gets the current binding for {@code GL_ELEMENT_ARRAY_BUFFER} and compares it
     * with this buffer's ID.
     * </p>
     *
     * @throws IllegalStateException if this index buffer is not bound.
     */
    private void ensureBound() {
        int current = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        if (current != iboId) {
            throw new IllegalStateException("IBO " + iboId + " is not bound. Current bound IBO is " + current);
        }
    }

    /**
     * Ensures that no index buffer is currently bound.
     * <p>
     * It checks that the {@code GL_ELEMENT_ARRAY_BUFFER} binding is 0
     * </p>
     *
     * @throws IllegalStateException if another index buffer is currently bound.
     */
    private void ensureNonBound() {
        int current = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        if (current != 0) {
            throw new IllegalStateException("IBO " + current + " is bound. No IBO should be bound");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the OpenGL ID of this index buffer.
     *
     * @return the buffer ID.
     */
    public int getIboId() {
        return iboId;
    }

    /**
     * Returns the number of indices stored in this buffer.
     *
     * @return the count of indices.
     */
    public int getCount() {
        return count;
    }
}

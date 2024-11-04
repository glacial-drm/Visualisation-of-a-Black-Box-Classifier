package com.blackbox.renderer;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {
    private int iboId;
    private int count;

    /** Creates a glBuffer and stores its id in iboId */
    public IndexBuffer(){
        iboId = glGenBuffers();
    }

    /**
     * Clears the allocated memory on the GPU
     * @return true if no error
     */
    public boolean destroy(){
        glDeleteBuffers(iboId);
        return true;
    }

    /**
     * Binds this Index buffer as a GL_ELEMENT_ARRAY_BUFFER
     * @return true if no errors
     */
    public boolean bind(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        return true;
    }

    /**
     * Unbinds this Index Buffer
     * @return true if no error
     */
    public boolean unbind(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        return true;
    }

    /**
     * Preps the data to be sent to the GPU and buffers it
     * @param indices list of indices for the model
     * @return true if no errors
     */
    public boolean storeIndexData(int[] indices){
        if(indices.length == 0 || indices.length % 3 != 0){
            throw new IllegalArgumentException("The number of indices must be a multiple of 3 and non 0");
        }

        count = indices.length;
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices);
        buffer.flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        return true;
    }

    public int getIboId() { return iboId; }
    public int getCount() { return count; }
}

package com.blackbox.renderer;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Vbo {
    private int vboId;

    public boolean create(){
        vboId = glGenBuffers();
        return true;
    }

    public boolean bind(){
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        return true;
    }

    public boolean unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return true;
    }

    public boolean storeFloatData(float[] data, int attribute){
        bind();
        FloatBuffer buf = addFloatData(data);
        glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
        glVertexAttribPointer(attribute, 3, GL_FLOAT, false, 0, 0);
        unbind();

        return true;
    }

    private FloatBuffer addFloatData(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public int getVboId(){
        return vboId;
    }
}

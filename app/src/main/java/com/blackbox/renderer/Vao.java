package com.blackbox.renderer;

import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Vao {
    private int vaoId;

    public boolean create(){
        vaoId = glGenVertexArrays();
        return true;
    }

    public boolean enableVertexAttrib(int attribute){
        glEnableVertexAttribArray(attribute);
        return true;
    }


    public boolean bind(){
        glBindVertexArray(vaoId);
        return true;
    }

    public boolean unbind(){
        glBindVertexArray(0);
        return true;
    }

    public boolean delete(){
        glDeleteBuffers(vaoId);
        return true;
    }

    /** Getter for the vaoID */
    public int getVaoId(){
        return vaoId;
    }
}

package com.blackbox;

import com.blackbox.renderer.IndexBuffer;
import com.blackbox.renderer.VertexArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class VertexArrayTest {
    VertexArray vao;
    @BeforeEach
    void init(){
        glfwInit();
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        vao = new VertexArray();
    }

    @Test
    void testInitBuffer(){
        VertexArray vao = new VertexArray();
        assertNotEquals(vao.getVaoId(), 0);
    }

    @Test
    void testDestroyBuffer(){
        assertTrue(vao.delete());
    }

    @Test
    void testBind(){
        assertTrue(vao.bind());
    }

    @Test
    void testUnbind(){
        assertTrue(vao.unbind());
    }


}

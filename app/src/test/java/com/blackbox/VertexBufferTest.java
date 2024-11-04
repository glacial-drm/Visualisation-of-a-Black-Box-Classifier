package com.blackbox;

import com.blackbox.renderer.IndexBuffer;
import com.blackbox.renderer.VertexBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class VertexBufferTest {
    VertexBuffer vbo;
    @BeforeEach
    void init(){
        glfwInit();
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        vbo = new VertexBuffer();
    }

    @Test
    void testInitBuffer(){
        VertexBuffer vbo = new VertexBuffer();
        assertNotEquals(vbo.getVboId(), 0);
    }

    @Test
    void testDestroyBuffer(){
        assertTrue(vbo.destroy());
    }

    @Test
    void testBind(){
        assertTrue(vbo.bind());
    }

    @Test
    void testUnbind(){
        assertTrue(vbo.unbind());
    }

    @Test
    void testAddFloatData(){
        float[] vertexs = {0.5f, 0.5f, 0.1f, 0.1f};
        vbo.storeFloatData(vertexs, 0, 2);
    }

    @Test
    void testStoreNothing(){
        float[] vertexs = {};
        assertThrows(IllegalArgumentException.class, () -> vbo.storeFloatData(vertexs, 0, 3));
    }

    @Test
    void testNonTriangleIndices(){
        float[] vertexs = {1.f,2.f};
        assertThrows(IllegalArgumentException.class, () -> vbo.storeFloatData(vertexs, 0, 3));
    }

    @Test
    void testDimensionBellowOne(){
        float[] vertexs = {0.5f, 0.5f, 0.1f, 0.1f};
        assertThrows(IllegalArgumentException.class, () -> vbo.storeFloatData(vertexs, 0, 0));

    }

    @Test
    void testDimensionAboveFour(){
        float[] vertexs = {0.5f, 0.5f, 0.1f, 0.1f};
        assertThrows(IllegalArgumentException.class, () -> vbo.storeFloatData(vertexs, 0, 5));

    }
}

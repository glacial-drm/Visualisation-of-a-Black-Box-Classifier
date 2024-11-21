package com.blackbox;

import com.blackbox.renderer.IndexBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class IndexBufferTest {
    IndexBuffer ibo;

    @BeforeEach
    void init(){
        if (!glfwInit()) {
            fail("Glfw failed to init");
        }
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (window == 0) {
            fail("Window failed to init");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        ibo = new IndexBuffer();
    }

    @Test
    void testInitBuffer() {
        IndexBuffer ibo = new IndexBuffer();
        assertNotEquals(ibo.getIboId(), 0);
    }

    @Test
    void testDestroyBuffer() {
        assertTrue(ibo.destroy());
    }

    @Test
    void testBind() {
        assertTrue(ibo.bind());
    }

    @Test
    void testUnbind() {
        assertTrue(ibo.unbind());
    }

    @Test
    void testStoreData() {
        int[] data = {1, 2, 3};
        ibo.storeIndexData(data);
        assertEquals(ibo.getCount(), 3);
    }

    @Test
    void testStoreNothing() {
        int[] data = {};
        assertThrows(IllegalArgumentException.class, () -> ibo.storeIndexData(data));
    }

    @Test
    void testNonTriangledata() {
        int[] data = {1, 2};
        assertThrows(IllegalArgumentException.class, () -> ibo.storeIndexData(data));
    }
}

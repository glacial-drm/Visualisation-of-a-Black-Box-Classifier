package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.VertexBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

class VertexBufferTest {
    long window;
    @BeforeEach
    void setUp() {
        glfwInit();
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        glfwFocusWindow(window);
        GL.createCapabilities();
    }

    @AfterEach
    void cleanUp() {
        // Clean up OpenGL context and GLFW
        glfwDestroyWindow(window);
        glfwTerminate();
    }

}
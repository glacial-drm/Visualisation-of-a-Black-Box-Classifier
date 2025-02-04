package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.IndexBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import java.rmi.NotBoundException;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;

class IndexBufferTest {
    long window;
    @BeforeEach
    void setUp() {
        glfwInit();
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterEach
    void cleanUp() {
        glBindBuffer(GL_INDEX_ARRAY, 0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.VertexArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

class VertexArrayTest {

    private long window;

    @BeforeEach
    void setUp() {
        glfwInit();
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterEach
    void cleanUp() {
        // Clean up OpenGL context and GLFW
        glBindVertexArray(0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    @Test
    void testVertexArrayCreation() {
        VertexArray vertexArray = new VertexArray();
        assertTrue(vertexArray.getVaoId() > 0, "VAO ID should be greater than 0");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after creating VAO");
    }

    @Test
    void testBind() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        assertEquals(vertexArray.getVaoId(), glGetInteger(GL_VERTEX_ARRAY_BINDING), "Bound VAO ID should match the created VAO ID");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after binding VAO");
    }

    @Test
    void testUnbind() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.unbind();
        assertEquals(0, glGetInteger(GL_VERTEX_ARRAY_BINDING), "VAO should be unbound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after unbinding VAO");
    }

    @Test
    void testEnableVertexAttrib() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.enableVertexAttrib(0);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after enabling vertex attribute");
    }

    @Test
    void testDisableVertexAttrib() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.enableVertexAttrib(0);
        vertexArray.disableVertexAttrib();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after disabling vertex attribute");
    }

    @Test
    void testDelete() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.delete();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after deleting VAO");
    }

    @Test
    void testBindWhenOtherBound() {
        VertexArray vertexArray1 = new VertexArray();
        VertexArray vertexArray2 = new VertexArray();
        vertexArray1.bind();

        // Ensure the assert statement in bind() fails when another VAO is bound
        assertThrows(AssertionError.class, vertexArray2::bind);
    }

    @Test
    void testUnbindWhenOtherBound() {
        VertexArray vertexArray1 = new VertexArray();
        VertexArray vertexArray2 = new VertexArray();
        vertexArray2.bind();

        // Ensure the assert statement in unbind() fails when another VAO is bound
        assertThrows(AssertionError.class, vertexArray1::unbind);
    }
}
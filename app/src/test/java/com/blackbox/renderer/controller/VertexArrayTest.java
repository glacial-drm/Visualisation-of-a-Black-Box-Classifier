package com.blackbox.renderer.controller;

import com.blackbox.renderer.controllers.VertexArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

class VertexArrayTest {

    private long window;

    @BeforeEach
    void setUp() {
        glfwInit();
        window = glfwCreateWindow(1, 1, "Testing", NULL, NULL);
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

    /// =======================================================================
    ///  CREATION TESTS
    /// =======================================================================

    @Test
    void testVertexArrayCreationNoBinds() {
        VertexArray vertexArray = new VertexArray();
        assertTrue(vertexArray.getVaoId() > 0, "Vao failed to init");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayCreationOtherBound() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();

        assertThrows(IllegalStateException.class, VertexArray::new);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayAfterUnbind() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.unbind();

        new VertexArray();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  DELETE TESTS
    /// =======================================================================

    @Test
    void testVertexDestroyNonBound() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.destroy();

        assertEquals(-1, vertexArray.getVaoId(), "VaoId did not change after delete");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexDestroyBound() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.destroy();

        assertEquals(-1, vertexArray.getVaoId(), "VaoId did not change after delete");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexDestroyOtherBound() {
        VertexArray vertexArray = new VertexArray();
        VertexArray vertexArray2 = new VertexArray();
        vertexArray2.bind();

        vertexArray.destroy();

        assertEquals(-1, vertexArray.getVaoId(), "VaoId did not change after delete");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  BIND TESTS
    /// =======================================================================

    @Test
    void testVertexArrayBindNoBinds() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayBindWhileBound() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();

        assertThrows(IllegalStateException.class, vertexArray::bind);

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayBindOtherBound() {
        VertexArray vertexArray = new VertexArray();
        VertexArray vertexArray2 = new VertexArray();
        vertexArray.bind();

        assertThrows(IllegalStateException.class, vertexArray2::bind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayBindUnbind() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.unbind();
        vertexArray.bind();

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  UNBIND TESTS
    /// =======================================================================

    @Test
    void testVertexArrayUnBindNoBinds() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayUnBindWhileNotBound() {
        VertexArray vertexArray = new VertexArray();

        assertThrows(IllegalStateException.class, vertexArray::unbind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayUnBindOtherBound() {
        VertexArray vertexArray = new VertexArray();
        VertexArray vertexArray2 = new VertexArray();
        vertexArray.bind();

        assertThrows(IllegalStateException.class, vertexArray2::unbind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexArrayUnBindAfterBind() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.unbind();
        vertexArray.bind();
        vertexArray.unbind();

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  ATTRIBUTE TESTS
    /// =======================================================================

    @Test
    void testSetVertexAttribWithoutBind() {
        VertexArray vertexArray = new VertexArray();
        // Attempt to set a vertex attribute without binding the VAO
        assertThrows(IllegalStateException.class, () -> {
            vertexArray.setVertexAttrib(0, 3, GL_FLOAT, false, 0, 0, false);
        });
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testSetVertexAttribWithBindNonInstanced() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        // Configure a non-instanced vertex attribute
        vertexArray.setVertexAttrib(0, 3, GL_FLOAT, false, 0, 0, false);
        vertexArray.enableVertexAttributes();
        vertexArray.disableVertexAttributes();
        vertexArray.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testSetVertexAttribWithBindInstanced() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        // Configure an instanced vertex attribute
        vertexArray.setVertexAttrib(1, 4, GL_FLOAT, true, 0, 0, true);
        vertexArray.enableVertexAttributes();
        vertexArray.disableVertexAttributes();
        vertexArray.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testEnableVertexAttributesWithoutBind() {
        VertexArray vertexArray = new VertexArray();
        // Even if there are no attributes set, calling enable should require the VAO to be bound.
        assertThrows(IllegalStateException.class, vertexArray::enableVertexAttributes);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testDisableVertexAttributesWithoutBind() {
        VertexArray vertexArray = new VertexArray();
        assertThrows(IllegalStateException.class, vertexArray::disableVertexAttributes);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testMultipleVertexAttribs() {
        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();
        // Configure multiple vertex attributes
        vertexArray.setVertexAttrib(0, 3, GL_FLOAT, false, 0, 0, false);
        vertexArray.setVertexAttrib(1, 4, GL_FLOAT, false, 0, 0, true);
        vertexArray.enableVertexAttributes();
        vertexArray.disableVertexAttributes();
        vertexArray.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }
}
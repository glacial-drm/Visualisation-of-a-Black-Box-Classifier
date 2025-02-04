package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.VertexBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

class VertexBufferTest {
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
        // Unbind any bound buffer before terminating the context
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    /// =======================================================================
    ///  CREATION TESTS
    /// =======================================================================

    @Test
    void testVertexBufferCreationNoBinds() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        assertTrue(VertexBuffer.getVboId() > 0, "VBO failed to init");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferCreationOtherBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();

        // Expect a new VertexBuffer creation to fail if one is already bound.
        assertThrows(IllegalStateException.class, VertexBuffer::new);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferAfterUnbind() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        VertexBuffer.unbind();

        new VertexBuffer();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  DESTROY TESTS
    /// =======================================================================

    @Test
    void testVertexDestroyNonBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.destroy();

        assertEquals(-1, VertexBuffer.getVboId(), "VBO id did not change after destroy");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexDestroyBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        VertexBuffer.destroy();

        assertEquals(-1, VertexBuffer.getVboId(), "VBO id did not change after destroy");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexDestroyOtherBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer VertexBuffer2 = new VertexBuffer();
        VertexBuffer2.bind();

        VertexBuffer.destroy();

        assertEquals(-1, VertexBuffer.getVboId(), "VBO id did not change after destroy");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  BIND TESTS
    /// =======================================================================

    @Test
    void testVertexBufferBindNoBinds() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferBindWhileBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();

        assertThrows(IllegalStateException.class, VertexBuffer::bind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferBindOtherBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer VertexBuffer2 = new VertexBuffer();
        VertexBuffer.bind();

        assertThrows(IllegalStateException.class, VertexBuffer2::bind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferBindUnbind() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        VertexBuffer.unbind();
        VertexBuffer.bind();

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  UNBIND TESTS
    /// =======================================================================

    @Test
    void testVertexBufferUnBindNoBinds() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        VertexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferUnBindWhileNotBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();

        assertThrows(IllegalStateException.class, VertexBuffer::unbind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferUnBindOtherBound() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer VertexBuffer2 = new VertexBuffer();
        VertexBuffer.bind();

        assertThrows(IllegalStateException.class, VertexBuffer2::unbind);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testVertexBufferUnBindAfterBind() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        VertexBuffer.unbind();
        VertexBuffer.bind();
        VertexBuffer.unbind();

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    /// =======================================================================
    ///  STORE FLOAT DATA TESTS
    /// =======================================================================

    @Test
    void testStoreFloatDataWithoutBind() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        float[] data = {0.0f, 1.0f, 2.0f};
        assertThrows(IllegalStateException.class, () -> VertexBuffer.storeFloatData(data));
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testStoreFloatDataWithBind() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        float[] data = {0.0f, 1.0f, 2.0f, 3.0f};
        VertexBuffer.storeFloatData(data);

        // Verify the uploaded data size (in bytes) using OpenGL's buffer parameter query.
        int expectedSize = data.length * Float.BYTES;
        int actualSize = glGetBufferParameteri(GL_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSize, actualSize, "Buffer size should match the size of the stored data");

        VertexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }

    @Test
    void testStoreFloatDataWithEmptyArray() {
        VertexBuffer VertexBuffer = new VertexBuffer();
        VertexBuffer.bind();
        float[] data = {};
        VertexBuffer.storeFloatData(data);

        int expectedSize = 0;
        int actualSize = glGetBufferParameteri(GL_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSize, actualSize, "Buffer size should be 0 for empty data");

        VertexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present");
    }
}
package com.blackbox.renderer;

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
    @Test
    void testVertexBufferCreation() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        assertTrue(vertexBuffer.getVboId() > 0, "VBO ID should be greater than 0");
    }

    @Test
    void testVertexBufferCreationFailure() {
        // Simulates a failure in the creation
        // As the error can only happen by creating too many buffers testing is not viable
        assertDoesNotThrow(() -> new VertexBuffer(), "VertexBuffer creation should not throw an exception");
    }

    @Test
    void testBind() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.bind();
        assertEquals(vertexBuffer.getVboId(), glGetInteger(GL_ARRAY_BUFFER_BINDING), "Bound buffer ID should match VBO ID");
    }

    @Test
    void testUnbind() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.bind();
        vertexBuffer.unbind();
        assertEquals(0, glGetInteger(GL_ARRAY_BUFFER_BINDING), "Buffer should be unbound");
    }

    @Test
    void testStoreFloatData() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        // Bind the buffer before calling storeFloatData
        vertexBuffer.bind();

        float[] data = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        int attribute = 0;
        int dimension = 3;

        // Ensure no OpenGL errors before calling storeFloatData
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present before storing float data");

        // Store float data
        vertexBuffer.storeFloatData(data, attribute, dimension);

        // Ensure no OpenGL errors after calling storeFloatData
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after storing float data");
    }

    @Test
    void testStoreFloatDataWithInvalidDimensionToBig() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        float[] data = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        int attribute = 0;
        int invalidDimension = 5; // Invalid dimension

        // Ensure the method throws an IllegalArgumentException for invalid dimension
        assertThrows(IllegalArgumentException.class, () -> {
            vertexBuffer.storeFloatData(data, attribute, invalidDimension);
        });
    }

    @Test
    void testStoreFloatDataWithInvalidDimensionToLow() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        float[] data = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        int attribute = 0;
        int invalidDimension = 0; // Invalid dimension

        // Ensure the method throws an IllegalArgumentException for invalid dimension
        assertThrows(IllegalArgumentException.class, () -> {
            vertexBuffer.storeFloatData(data, attribute, invalidDimension);
        });
    }

    @Test
    void testStoreFloatDataWithInvalidData() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        float[] invalidData = {0.0f, 1.0f}; // Not a multiple of dimension
        int attribute = 0;
        int dimension = 3;

        // Ensure the method throws an IllegalArgumentException for invalid data
        assertThrows(IllegalArgumentException.class, () -> {
            vertexBuffer.storeFloatData(invalidData, attribute, dimension);
        });
    }

    @Test
    void testStoreFloatDataWithNoData() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        float[] invalidData = {}; // Not a multiple of dimension
        int attribute = 0;
        int dimension = 3;

        // Ensure the method throws an IllegalArgumentException for invalid data
        assertThrows(IllegalArgumentException.class, () -> {
            vertexBuffer.storeFloatData(invalidData, attribute, dimension);
        });
    }


    @Test
    void testStoreFloatDataWithoutBinding() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.bind();
        float[] data = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        int attribute = 0;
        int dimension = 3;

        // Ensure the buffer is not bound
        vertexBuffer.unbind();

        // Ensure the assert statement in storeFloatData fails when the buffer is not bound
        assertThrows(AssertionError.class, () -> {
            vertexBuffer.storeFloatData(data, attribute, dimension);
        });
    }

    @Test
    void testDestroy() {
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.destroy();

        // Check for OpenGL errors after destroying the buffer
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after destroying the buffer");
    }
}
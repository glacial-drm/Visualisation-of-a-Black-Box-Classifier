package com.blackbox.renderer;

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

    @BeforeEach
    void setUp() {
        glfwInit();
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterEach
    void cleanUp() {
        glBindBuffer(GL_INDEX_ARRAY, 0);
    }

    @Test
    void testIndexBufferCreation() {
        IndexBuffer indexBuffer = new IndexBuffer();
        assertTrue(indexBuffer.getIboId() > 0, "IBO ID should be greater than 0");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after creating buffer");
    }

    @Test
    void testBind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        assertEquals(indexBuffer.getIboId(), glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING), "Bound buffer ID should match IBO ID");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after binding buffer");
    }

    @Test
    void testMultipleBind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        IndexBuffer indexBuffer2 = new IndexBuffer();
        indexBuffer2.bind();

        assertEquals(indexBuffer2.getIboId(), glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING), "Bound buffer ID should match IBO ID");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen after binding multiple buffer");
    }

    @Test
    void testUnbind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.unbind();

        assertEquals(0, glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING), "Buffer should be unbound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL unbinding");
    }

    @Test
    void testBindWhenOtherBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        IndexBuffer indexBuffer2 = new IndexBuffer();

        // Ensure the assert statement in storeIndexData fails when the buffer is not bound
        AssertionError assertionError = assertThrows(AssertionError.class, () -> {
            indexBuffer2.bind();
        });
    }

    @Test
    void testUnbindWhenOtherBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        IndexBuffer indexBuffer2 = new IndexBuffer();
        indexBuffer2.bind();

        // Ensure the assert statement in storeIndexData fails when the buffer is not bound
        AssertionError assertionError = assertThrows(AssertionError.class, () -> {
            indexBuffer.unbind();
        });
    }

    @Test
    void testStoreIndexDataUnBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        int[] indices = {0, 1, 2, 3, 4, 5};

        // Ensure the assert statement in storeIndexData fails when the buffer is not bound
        AssertionError assertionError = assertThrows(AssertionError.class, () -> {
            indexBuffer.storeIndexData(indices);
        });
    }

    @Test
    void testStoreNoData() throws NotBoundException {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        int[] indices = {};
        Exception assertionError = assertThrows(IllegalArgumentException.class, () -> {
            indexBuffer.storeIndexData(indices);
        });
    }


    @Test
    void testStoreIndexDataBound() throws NotBoundException {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        int[] indices = {0, 1, 2, 3, 4, 5};
        indexBuffer.storeIndexData(indices);
        assertEquals(indices.length, indexBuffer.getCount(), "Count should match the number of indices");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL storing data");
    }

    @Test
    void testStoreIndexDataWithInvalidInput() {
        IndexBuffer indexBuffer = new IndexBuffer();
        int[] invalidIndices = {0, 1}; // Not a multiple of 3

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            indexBuffer.storeIndexData(invalidIndices);
        });

        String expectedMessage = "The number of indices must be a multiple of 3 and non 0";

        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should match expected message");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL storing data");
    }

    @Test
    void testDestroy() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.destroy();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after destroying the buffer");
    }
}
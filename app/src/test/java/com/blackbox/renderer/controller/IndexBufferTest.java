package com.blackbox.renderer.controller;

import com.blackbox.renderer.controllers.IndexBuffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.*;

class IndexBufferTest {

    private long window;

    @BeforeEach
    void setUp() {
        // Initialize GLFW and create a window with an OpenGL context for testing.
        glfwInit();
        window = glfwCreateWindow(1, 1, "Testing IndexBuffer", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterEach
    void cleanUp() {
        // Unbind any bound element array buffer before destroying the context.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    // =========================================================================
    // CREATION TESTS
    // =========================================================================

    @Test
    void testIndexBufferCreationNoBinds() {
        IndexBuffer indexBuffer = new IndexBuffer();
        // The buffer ID should be a positive integer.
        assertTrue(indexBuffer.getIboId() > 0, "IndexBuffer creation failed: IboId is not positive");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after IndexBuffer creation");
    }

    @Test
    void testIndexBufferCreationOtherBound() {
        // Create an IndexBuffer and bind it.
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();

        // Attempting to create a new IndexBuffer while one is bound should throw an exception.
        assertThrows(IllegalStateException.class, IndexBuffer::new,
                "Creating an IndexBuffer while another is bound should throw an exception");

        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after unbinding");
    }

    @Test
    void testIndexBufferAfterUnbind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.unbind();

        // After unbinding, creating a new IndexBuffer should succeed.
        IndexBuffer newIndexBuffer = new IndexBuffer();
        assertTrue(newIndexBuffer.getIboId() > 0, "New IndexBuffer creation failed after unbinding the previous one");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after new IndexBuffer creation");
    }

    // =========================================================================
    // DESTROY TESTS
    // =========================================================================

    @Test
    void testIndexBufferDestroyNonBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.destroy();
        // After destroy, the buffer ID should be set to -1.
        assertEquals(-1, indexBuffer.getIboId(), "IndexBuffer destroy did not set IboId to -1 for non-bound buffer");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after destroying IndexBuffer");
    }

    @Test
    void testIndexBufferDestroyBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.destroy();
        // Even when the buffer is bound, destruction should correctly set the iboId to -1.
        assertEquals(-1, indexBuffer.getIboId(), "IndexBuffer destroy did not set IboId to -1 for bound buffer");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after destroying bound IndexBuffer");
    }

    @Test
    void testIndexBufferDestroyOtherBound() {
        IndexBuffer indexBuffer1 = new IndexBuffer();
        IndexBuffer indexBuffer2 = new IndexBuffer();
        indexBuffer2.bind();

        indexBuffer1.destroy();
        // Destroying one IndexBuffer when another is bound should still work fine.
        assertEquals(-1, indexBuffer1.getIboId(), "IndexBuffer destroy did not set IboId to -1 when another buffer is bound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after destroying IndexBuffer with another bound");
    }

    // =========================================================================
    // BIND TESTS
    // =========================================================================

    @Test
    void testIndexBufferBindNoBinds() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        // Verify that the current binding for GL_ELEMENT_ARRAY_BUFFER matches the iboId.
        int currentBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertEquals(indexBuffer.getIboId(), currentBinding, "IndexBuffer did not bind correctly");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after binding IndexBuffer");
    }

    @Test
    void testIndexBufferBindWhileBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        // Re-binding the same IndexBuffer should result in an exception.
        assertThrows(IllegalStateException.class, indexBuffer::bind,
                "Binding IndexBuffer while already bound should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting a redundant bind");
    }

    @Test
    void testIndexBufferBindOtherBound() {
        IndexBuffer indexBuffer1 = new IndexBuffer();
        IndexBuffer indexBuffer2 = new IndexBuffer();
        indexBuffer1.bind();
        // Attempting to bind a different IndexBuffer while one is already bound should throw an exception.
        assertThrows(IllegalStateException.class, indexBuffer2::bind,
                "Binding another IndexBuffer while one is already bound should throw an exception");
        indexBuffer1.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after unbinding IndexBuffer");
    }

    @Test
    void testIndexBufferBindUnbind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.unbind();
        // After unbinding, binding again should succeed.
        indexBuffer.bind();
        int currentBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertEquals(indexBuffer.getIboId(), currentBinding, "IndexBuffer did not re-bind correctly after unbinding");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after re-binding IndexBuffer");
    }

    // =========================================================================
    // UNBIND TESTS
    // =========================================================================

    @Test
    void testIndexBufferUnbindNoBinds() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.unbind();
        // The binding should be zero after unbinding.
        int currentBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertEquals(0, currentBinding, "IndexBuffer did not unbind correctly; current binding is not 0");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after unbinding IndexBuffer");
    }

    @Test
    void testIndexBufferUnbindWhileNotBound() {
        IndexBuffer indexBuffer = new IndexBuffer();
        // Calling unbind without binding should throw an exception.
        assertThrows(IllegalStateException.class, indexBuffer::unbind,
                "Unbinding an IndexBuffer that is not bound should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting unbind on non-bound IndexBuffer");
    }

    @Test
    void testIndexBufferUnbindOtherBound() {
        IndexBuffer indexBuffer1 = new IndexBuffer();
        IndexBuffer indexBuffer2 = new IndexBuffer();
        indexBuffer1.bind();
        // Attempting to unbind a different, non-bound IndexBuffer should throw an exception.
        assertThrows(IllegalStateException.class, indexBuffer2::unbind,
                "Unbinding a different IndexBuffer that is not bound should throw an exception");
        indexBuffer1.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after unbinding IndexBuffer");
    }

    @Test
    void testIndexBufferUnbindAfterBind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        // Execute multiple bind/unbind cycles.
        indexBuffer.bind();
        indexBuffer.unbind();
        indexBuffer.bind();
        indexBuffer.unbind();
        int currentBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertEquals(0, currentBinding, "IndexBuffer did not unbind correctly after multiple bind/unbind cycles");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after multiple bind/unbind cycles");
    }

    // =========================================================================
    // storeIndexData TESTS
    // =========================================================================

    @Test
    void testStoreIndexDataWithoutBind() {
        IndexBuffer indexBuffer = new IndexBuffer();
        int[] indices = {0, 1, 2};
        // Calling storeIndexData without binding should throw an IllegalStateException.
        assertThrows(IllegalStateException.class,
                () -> indexBuffer.storeIndexData(indices),
                "storeIndexData without binding should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting storeIndexData without bind");
    }

    @Test
    void testStoreIndexDataEmptyArray() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        int[] emptyIndices = {};
        // An empty array is invalid and should result in an IllegalArgumentException.
        assertThrows(IllegalArgumentException.class,
                () -> indexBuffer.storeIndexData(emptyIndices),
                "storeIndexData with empty array should throw an exception");
        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting storeIndexData with empty array");
    }

    @Test
    void testStoreIndexDataNotMultipleOfThree() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        int[] invalidIndices = {0, 1}; // Length is 2, not a multiple of 3.
        // The method should throw an IllegalArgumentException.
        assertThrows(IllegalArgumentException.class,
                () -> indexBuffer.storeIndexData(invalidIndices),
                "storeIndexData with array length not a multiple of 3 should throw an exception");
        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting storeIndexData with invalid array length");
    }

    @Test
    void testStoreIndexDataValidArray() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        // Use a valid index array with length being a multiple of 3.
        int[] validIndices = {0, 1, 2, 2, 3, 0};
        indexBuffer.storeIndexData(validIndices);
        // Verify that the count is updated correctly.
        assertEquals(validIndices.length, indexBuffer.getCount(), "IndexBuffer count does not match the number of stored indices");
        // Verify the uploaded data size using glGetBufferParameteri.
        int expectedSize = validIndices.length * Integer.BYTES;
        int actualSize = glGetBufferParameteri(GL_ELEMENT_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSize, actualSize, "Buffer size does not match the expected size of the stored index data");
        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after storing valid index data");
    }

    @Test
    void testStoreIndexDataMultipleCalls() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        // First, store one set of valid indices.
        int[] indicesFirst = {0, 1, 2};
        indexBuffer.storeIndexData(indicesFirst);
        assertEquals(indicesFirst.length, indexBuffer.getCount(), "First call: count mismatch");
        int expectedSizeFirst = indicesFirst.length * Integer.BYTES;
        int actualSizeFirst = glGetBufferParameteri(GL_ELEMENT_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSizeFirst, actualSizeFirst, "First call: Buffer size mismatch");

        // Then, store a different set of valid indices.
        int[] indicesSecond = {3, 4, 5, 6, 7, 8};
        indexBuffer.storeIndexData(indicesSecond);
        assertEquals(indicesSecond.length, indexBuffer.getCount(), "Second call: count mismatch");
        int expectedSizeSecond = indicesSecond.length * Integer.BYTES;
        int actualSizeSecond = glGetBufferParameteri(GL_ELEMENT_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSizeSecond, actualSizeSecond, "Second call: Buffer size mismatch");
        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after multiple storeIndexData calls");
    }

    @Test
    void testStoreIndexDataEdgeCaseLargeArray() {
        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        // Create a large index array where the length is a multiple of 3.
        int largeCount = 300; // 300 is a multiple of 3.
        int[] largeIndices = new int[largeCount];
        for (int i = 0; i < largeCount; i++) {
            largeIndices[i] = i;
        }
        indexBuffer.storeIndexData(largeIndices);
        assertEquals(largeCount, indexBuffer.getCount(), "Large array: count mismatch");
        int expectedSize = largeCount * Integer.BYTES;
        int actualSize = glGetBufferParameteri(GL_ELEMENT_ARRAY_BUFFER, GL_BUFFER_SIZE);
        assertEquals(expectedSize, actualSize, "Large array: Buffer size mismatch");
        indexBuffer.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after storing large index data");
    }
}

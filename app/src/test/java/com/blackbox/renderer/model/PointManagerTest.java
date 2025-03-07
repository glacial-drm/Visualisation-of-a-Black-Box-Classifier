package com.blackbox.renderer.model;

import com.blackbox.model.PointData;
import com.blackbox.renderer.model.PointManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

class PointManagerTest {

    private long window;

    @BeforeEach
    void setUp() {
        // Initialize GLFW and create a minimal window with an OpenGL context.
        glfwInit();
        window = glfwCreateWindow(1, 1, "Test PointManager", 0, 0);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterEach
    void tearDown() {
        // Unbind any VAO and buffers.
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    @Test
    void testInitialState() {
        PointManager pm = new PointManager();
        // Initially, no points have been added.
        assertEquals(0, pm.getPointCount(), "Initial point count should be 0");
        assertFalse(pm.isPoints(), "isPoints() should return false when no points have been added");
        // The index buffer is created during initialization and should have 6 indices (for the quad).
        assertEquals(6, pm.getIndicesLength(), "Index buffer should contain 6 indices");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after initialization");
    }

    @Test
    void testAddSinglePoint() {
        PointManager pm = new PointManager();
        PointData pd = new PointData(new float[] { 0.1f, 0.2f, 0.2f }, 1);
        pm.addPoint(pd, false);
        assertEquals(1, pm.getPointCount(), "Point count should be 1 after adding one point");
        assertTrue(pm.isPoints(), "isPoints() should return true when a point has been added");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testAddMultiplePoints() {
        PointManager pm = new PointManager();
        int numPoints = 5;
        for (int i = 0; i < numPoints; i++) {
            PointData pd = new PointData(new float[] { 0.1f * i, 0.2f * i,0.2f * i }, i % 2);
            pm.addPoint(pd, false);
        }
        assertEquals(numPoints, pm.getPointCount(), "Point count should equal the number of points added");
        assertTrue(pm.isPoints(), "no points are stored");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testIndicesLengthConstant() {
        PointManager pm = new PointManager();

        assertEquals(6, pm.getIndicesLength(), "Index buffer length should always be 6");

        for (int i = 0; i < 3; i++) {
            PointData pd = new PointData(new float[] { 0.1f * i, 0.2f * i, 0.2f * i }, 0);
            pm.addPoint(pd, false);
        }
        assertEquals(6, pm.getIndicesLength(), "Index buffer length should remain constant regardless of points added");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testPrepPointsBindsVAOAndIndexBuffer() {
        PointManager pm = new PointManager();

        PointData pd = new PointData(new float[] { 0.5f, 0.5f, 0.5f }, 1);
        pm.addPoint(pd, false);
        pm.addPoint(pd, false);


        pm.prepPoints();
        int vaoBinding = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int indexBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertNotEquals(0, vaoBinding, "After prepPoints(), a VAO should be bound");
        assertNotEquals(0, indexBinding, "After prepPoints(), an index buffer should be bound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testCleanUpUnbinds() {
        PointManager pm = new PointManager();

        PointData pd = new PointData(new float[] { 0.5f, 0.5f, 0.5f }, 1);
        pm.addPoint(pd, false);
        pm.prepPoints();

        pm.cleanUp();
        int vaoBinding = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int indexBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertEquals(0, vaoBinding, "After cleanUp(), no VAO should be bound");
        assertEquals(0, indexBinding, "After cleanUp(), no index buffer should be bound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testPrepPointsWithNoPoints() {
        PointManager pm = new PointManager();
        // Without adding any points, call prepPoints().
        pm.prepPoints();
        int vaoBinding = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int indexBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        // Even if no points have been added, the VAO and index buffer should be bound.
        assertNotEquals(0, vaoBinding, "Even with no points, a VAO should be bound after prepPoints()");
        assertNotEquals(0, indexBinding, "Even with no points, an index buffer should be bound after prepPoints()");
        pm.cleanUp();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }

    @Test
    void testAddAndPrepPointsConsistency() {
        PointManager pm = new PointManager();
        int initialCount = pm.getPointCount();
        // Add several points.
        for (int i = 0; i < 7; i++) {
            PointData pd = new PointData(new float[] { i * 0.1f,i * 0.5f , i * 0.5f }, i % 2);
            pm.addPoint(pd, false);
        }
        assertEquals(initialCount + 7, pm.getPointCount(), "Point count should update correctly after adding points");

        pm.prepPoints();
        int vaoBinding = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        int indexBinding = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        assertNotEquals(0, vaoBinding, "After prepPoints(), the VAO should be bound");
        assertNotEquals(0, indexBinding, "After prepPoints(), the index buffer should be bound");

        pm.cleanUp();
        assertEquals(0, glGetInteger(GL_VERTEX_ARRAY_BINDING), "After cleanUp(), no VAO should be bound");
        assertEquals(0, glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING), "After cleanUp(), no index buffer should be bound");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should happen");
    }
}

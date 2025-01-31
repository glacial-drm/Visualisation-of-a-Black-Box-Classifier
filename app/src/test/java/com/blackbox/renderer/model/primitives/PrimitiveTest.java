package com.blackbox.renderer.model.primitives;

import javafx.scene.paint.Color;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

class PrimitiveTest {
    private TestPrimitive primitive;

    @BeforeEach
    void setUp() {
        Vector3f position = new Vector3f(0, 0, 0);
        Vector3f rotation = new Vector3f(0, 0, 0);
        float scale = 1.0f;
        Color colour = Color.RED;
        float[] vertexData = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        int[] indices = {0, 1, 2};

        primitive = new TestPrimitive(position, rotation, scale, colour, vertexData, indices);
    }

    @Test
    void testGetPosition() {
        assertEquals(new Vector3f(0, 0, 0), primitive.getPosition(), "Position should match the initial value");
    }

    @Test
    void testGetRotation() {
        assertEquals(new Vector3f(0, 0, 0), primitive.getRotation(), "Rotation should match the initial value");
    }

    @Test
    void testGetScale() {
        assertEquals(1.0f, primitive.getScale(), 0.0001f, "Scale should match the initial value");
    }

    @Test
    void testMove() {
        primitive.move(1.0f, 2.0f, 3.0f);
        assertEquals(new Vector3f(1.0f, 2.0f, 3.0f), primitive.getPosition(), "Position should update correctly after move");
    }

    @Test
    void testRotate() {
        primitive.rotate(10.0f, 20.0f, 30.0f);
        assertEquals(new Vector3f(10.0f, 20.0f, 30.0f), primitive.getRotation(), "Rotation should update correctly after rotate");
    }

    @Test
    void testScale() {
        primitive.scale(2.0f);
        assertEquals(3.0f, primitive.getScale(), 0.0001f, "Scale should update correctly after scaling");
    }

    @Test
    void testGetVertices() {
        float[] expectedVertices = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        assertArrayEquals(expectedVertices, primitive.getVertices(), "Vertices should match the initial data");
    }

    @Test
    void testGetIndices() {
        int[] expectedIndices = {0, 1, 2};
        assertArrayEquals(expectedIndices, primitive.getIndices(), "Indices should match the initial data");
    }

    @Test
    void enableAttributes() {
        primitive.attributes.add(0);
        primitive.attributes.add(1);
        primitive.attributes.add(2);

        primitive.enableAttributes();

        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after storing float data");
    }

    @Test
    void testGetColour() {
        Vector3f expectedColour = new Vector3f(1.0f, 0.0f, 0.0f); // RED
        assertEquals(expectedColour, primitive.getColour(), "Colour should match the initial value");
    }

    @Test
    void testEnableAttributes() {
        // Ensure the enableAttributes function does not throw errors
        primitive.enableAttributes();
        assertTrue(true, "enableAttributes executed without errors");
    }

    @Test
    void testDisable() {
        // Ensure the disable function does not throw errors
        primitive.disable();
        assertTrue(true, "disable executed without errors");
    }

    @Test
    void testGetIndexCount() {
        assertEquals(3, primitive.getIndexCount(), "Index count should match the number of indices");
    }
}

package com.blackbox.renderer.model.primitives;

import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.controlers.Shader;
import com.blackbox.renderer.model.primitives.Disk;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DiskTest {

    @Test
    void testDiskConstructor() {
        Vector3f position = new Vector3f(1.0f, 2.0f, 3.0f);
        float radius = 5.0f;
        Vector3f rotation = new Vector3f(0.0f, 90.0f, 0.0f);
        float scale = 2.0f;
        Color color = Color.RED;

        Disk disk = new Disk(position, radius, rotation, scale, color);

        assertEquals(position, disk.getPosition(), "Position should be correctly initialized.");
        assertEquals(rotation, disk.getRotation(), "Rotation should be correctly initialized.");
        assertEquals(scale, disk.getScale(), "Scale should be correctly initialized.");
        assertEquals(radius, disk.getRadius(), "Radius should be correctly initialized.");
        assertEquals(new Vector3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue()),
                disk.getColour(), "Color should be correctly initialized.");
        assertEquals(3, disk.getDimension(), "Dimension should be set to 3.");
    }

    @Test
    void testSetUpVertexBuffer() {
        Vector3f position = new Vector3f(1.0f, 1.0f, 1.0f);
        float radius = 5.0f;
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        float scale = 1.0f;
        Color color = Color.BLUE;

        Disk disk = new Disk(position, radius, rotation, scale, color);

        assertNotNull(disk.vertexBuffer, "VertexBuffer should be initialized.");
        assertTrue(disk.attributes.contains(0), "Attribute 0 should be added to the attributes list.");
    }

    @Test
    void testUploadUniforms() {
        // Arrange
        Disk disk = new Disk(new Vector3f(0, 0, 0), 1.0f, new Vector3f(0, 0, 0), 1.0f, javafx.scene.paint.Color.RED);
        Shader mockShader = mock(Shader.class);
        Matrix4f mockTransformMatrix = new Matrix4f();

        // Mock static method
        try (MockedStatic<Camera> mockedCamera = mockStatic(Camera.class)) {
            mockedCamera.when(() -> Camera.createTransformMatrix(disk.getPosition(), disk.getRotation(), disk.getScale()))
                    .thenReturn(mockTransformMatrix);

            // Act
            disk.uploadUniforms(mock(Camera.class), mockShader);

            // Assert
            mockedCamera.verify(() -> Camera.createTransformMatrix(disk.getPosition(), disk.getRotation(), disk.getScale()));
            verify(mockShader).loadUniform("transformMatrix", mockTransformMatrix);
            verify(mockShader).loadUniform("colour", disk.getColour());
        }
    }

    @Test
    void testGetRadius() {
        Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
        float radius = 10.0f;
        Vector3f rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        float scale = 1.0f;
        Color color = Color.YELLOW;

        Disk disk = new Disk(position, radius, rotation, scale, color);

        assertEquals(radius, disk.getRadius(), "getRadius() should return the correct radius.");
    }
}

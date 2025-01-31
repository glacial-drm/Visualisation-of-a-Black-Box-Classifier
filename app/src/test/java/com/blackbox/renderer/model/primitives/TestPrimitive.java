package com.blackbox.renderer.model.primitives;

import com.blackbox.renderer.controlers.Camera;
import com.blackbox.renderer.controlers.Shader;
import com.blackbox.renderer.model.primitives.Primitive;
import javafx.scene.paint.Color;
import org.joml.Vector3f;

public class TestPrimitive extends Primitive {
    public TestPrimitive(Vector3f pos, Vector3f rot, float scale, Color colour, float[] VertexData, int[] indicesData) {
        super(pos, rot, scale, colour, VertexData, indicesData);
    }

    @Override
    public void uploadUniforms(Camera camera, Shader shader) {
        // Dummy implementation for testing
    }

    @Override
    protected void setUpVertexBuffer() {
        // Dummy implementation for testing
    }
}

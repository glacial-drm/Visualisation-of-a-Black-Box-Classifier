package com.blackbox.renderer;

import com.blackbox.renderer.primitives.Disk;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private int sWidth, sHeight;

    /**
     * the main render loop
     * takes no data and returns no data
     * Handles clearing the screen, setting up buffers, creating shaders and rendering the science
     */
    public void render() {
        clearScreen();

        Disk d = new Disk();
        d.create(new Vector3f(0,0,0), 1, new Vector3f(0,0,0), 1, Color.lightGray);

        Shader shader = new Shader();
        boolean valid = shader.create("vertex.glsl", "fragment.glsl");
        shader.setActive();

        Camera camera = new Camera();

        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(d.getIndices());

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.storeFloatData(d.getVertices(), 0, d.getDimension());
        vertexBuffer.bind();

        Matrix4f transformationMatrix = camera.createTransformMatrix(d.getPosition(), d.getRotation(), d.getScale());
        shader.loadMatrixUniform(transformationMatrix);

        vertexArray.enableVertexAttrib(0);
        glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, 0);
        vertexArray.disableVertexAttrib();
        vertexArray.unbind();
    }

    /**
     * Changes the viewport of the program
     *
     * @param width  must be less than the screen width
     * @param height must be less than the screen height
     */
    public void resize(int width, int height) {
        // Could check args are less than screen size
        sWidth = width;
        sHeight = height;

        try {
            glViewport(0, 0, sWidth, sHeight);
        } catch (Exception ignored) {
        }
    }

    /**
     * Handles clearing the opengl buffers and resting the screen to a default colour
     */
    private void clearScreen() {
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    // Getters and setter mostly used for testing

    /**
     * Gets the viewport width
     */
    public int getWidth() {
        return sWidth;
    }

    /**
     * Gets the viewport height
     */
    public int getHeight() {
        return sHeight;
    }
}

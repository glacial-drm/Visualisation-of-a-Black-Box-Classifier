package com.blackbox.renderer;

import com.blackbox.renderer.primitives.Disk;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private int sWidth, sHeight;
    private Camera camera;
    private Set<Integer> moveKeys = Set.of(GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D);
    /**
     * the main render loop
     * takes no data and returns no data
     * Handles clearing the screen, setting up buffers, creating shaders and rendering the science
     */
    public void render() {
        clearScreen();

        Disk d = new Disk();
        d.create(new Vector3f(0, 0, -1), 1, new Vector3f(0, 0, 0), 0.5f, Color.ALICEBLUE);

        Shader shader = new Shader();
        boolean valid = shader.create("vertex.glsl", "fragment.glsl");
        shader.setActive();

        VertexArray vertexArray = new VertexArray();
        vertexArray.bind();

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.bind();
        indexBuffer.storeIndexData(d.getIndices());

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.storeFloatData(d.getVertices(), 0, d.getDimension());
        vertexBuffer.bind();

        Matrix4f transformationMatrix = Camera.createTransformMatrix(d.getPosition(), d.getRotation(), d.getScale());

        shader.loadTransformMatrix(transformationMatrix);
        shader.loadProjectionMatrix(camera.getProjectionMatrix());
        shader.loadViewMatrix(camera.getViewMatrix());

        vertexArray.enableVertexAttrib(0);
        glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, 0);
        vertexArray.disableVertexAttrib();
        vertexArray.unbind();
    }

    public Set<Integer> getMovementKeys(){
        return moveKeys;
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

        glViewport(0, 0, sWidth, sHeight);
        camera = new Camera(sWidth, sHeight, 70, 0, 100);
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

    public void move(int key) {
        camera.move(key);
    }
}

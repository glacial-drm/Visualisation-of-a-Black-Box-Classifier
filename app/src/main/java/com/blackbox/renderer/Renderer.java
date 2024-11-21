package com.blackbox.renderer;

import com.blackbox.renderer.primitives.Disk;
import com.blackbox.renderer.primitives.Primitive;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private int sWidth, sHeight;
    private Camera camera;
    private final HashMap<Class<?>, Shader> shaders = new HashMap<Class<?>, Shader>();
    private final ArrayList<Primitive> objects = new ArrayList<Primitive>();

    /**
     * Resizes the window to the given size <br>
     * initialise the shaders and creates there shader programs <br>
     * Uploads the projection and view matrix
     *
     * @param width size of screen
     * @param height size of screen
     */
    public Renderer(int width, int height) {
        initShaders();
        resize(width, height);
        uploadProjectionMatrix();
        uploadViewMatrix();
        loadJunkData();
    }

    /**
     * the main render loop
     * Handles clearing the screen, uploading uniforms and rendering objects
     */
    public void render() {
        clearScreen();
        move();

        for (Primitive obj : objects) {
            obj.uploadUniforms(camera, shaders.get(obj.getClass()));
            obj.enableAttributes();
            glDrawElements(GL_TRIANGLES, obj.getIndexCount(), GL_UNSIGNED_INT, 0);
            obj.disable();
        }
    }

    // Function made until the sampler can pass through points
    private void loadJunkData() {
        objects.add(new Disk(new Vector3f(0, 0, 0), 0.5f, new Vector3f(0, 0, 0), 1f, Color.BLUE));
        objects.add(new Disk(new Vector3f(1.5f, 0, 0), 0.5f, new Vector3f(0, 0, 0), 1f, Color.RED));
        objects.add(new Disk(new Vector3f(-1.5f, 0, 0), 0.5f, new Vector3f(0, 0, 0), 1f, Color.GREEN));
        objects.add(new Disk(new Vector3f(-2.1f, 2.4f, 0), 0.5f, new Vector3f(0, 0, 0), 1f, Color.BLACK));
    }
    
    /** Uploads the projection matrix to all the shaders */
    private void uploadProjectionMatrix() {
        for (Shader shader : shaders.values()) {
            if(shader.isWorldBased())
                shader.loadMatrixUniform("projectionMatrix", camera.getProjectionMatrix());
        }
    }

    /** Uploads the view matrix to all the shaders */
    private void uploadViewMatrix() {
        for (Shader shader : shaders.values()) {
            if(shader.isWorldBased())
                shader.loadMatrixUniform("viewMatrix", camera.getViewMatrix());
        }
    }

    /**
     * Loads in the shaders from their files and stores them in a hashmap with key of
     * {@link com.blackbox.renderer.primitives.Primitive primitve type} and value as the new
     * {@link Shader shader}
     */
    private void initShaders() {
        // Load the disk shader
        Shader diskShader = new Shader(true);
        diskShader.create("vertex.glsl", "fragment.glsl");
        shaders.put(Disk.class, diskShader);
    }


    /**
     * Changes the viewport of the program
     *
     * @param width  must be less than the screen width
     * @param height must be less than the screen height
     */
    public void resize(int width, int height) {
        sWidth = width;
        sHeight = height;

        glViewport(0, 0, sWidth, sHeight);

        camera = (camera != null) ? camera.resize(sWidth, sHeight) : new Camera(sWidth, sHeight, 70, 0, 100);

        uploadProjectionMatrix();
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

    /** Calls the {@link com.blackbox.renderer.Camera move function with the given key */
    public void move() {
        if(camera.move()){
            uploadViewMatrix();
        }
    }
}

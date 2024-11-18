package com.blackbox.renderer;

import com.blackbox.renderer.primitives.Disk;
import com.blackbox.renderer.primitives.Primitive;
import com.blackbox.renderer.primitives.PrimitiveTypes;
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
    private final Set<Integer> moveKeys = Set.of(GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D);
    private int sWidth, sHeight;
    private Camera camera;
    private final HashMap<PrimitiveTypes, Shader> shaders = new HashMap<PrimitiveTypes, Shader>();
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
        for (Primitive obj : objects) {
            Shader shader = shaders.get(obj.getType());

            Matrix4f transformationMatrix = Camera.createTransformMatrix(obj.getPosition(), obj.getRotation(), obj.getScale());
            shader.loadMatrixUniform("transformMatrix", transformationMatrix);
            shader.loadVector3d("colour", obj.getColour());

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
            shader.loadMatrixUniform("projectionMatrix", camera.getProjectionMatrix());
        }
    }

    /** Uploads the view matrix to all the shaders */
    private void uploadViewMatrix() {
        for (Shader shader : shaders.values()) {
           shader.loadMatrixUniform("viewMatrix", camera.getViewMatrix());
        }
    }

    /**
     * Loads in the shaders from their files and stores them in a hashmap with key of
     * {@link com.blackbox.renderer.primitives.PrimitiveTypes primitve type} and value as the new
     * {@link Shader shader}
     */
    private void initShaders() {
        // Load the disk shader
        Shader diskShader = new Shader();
        diskShader.create("vertex.glsl", "fragment.glsl");
        shaders.put(PrimitiveTypes.DISK, diskShader);
    }


    /**
     * Changes the viewport of the program
     *
     * @param width  must be less than the screen width
     * @param height must be less than the screen height
     */
    public void resize(int width, int height) {
        System.out.println("RESIZE");
        // Could check args are less than screen size
        sWidth = width;
        sHeight = height;

        glViewport(0, 0, sWidth, sHeight);
        camera = new Camera(sWidth, sHeight, 70, 0, 100);
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

    /** Returns all the valid GLFW keys for movement W,A,S,D*/
    public Set<Integer> getMovementKeys() {
        return moveKeys;
    }

    // TODO: Make it work with multiple keys and state which keys are active
    /** Calls the {@link com.blackbox.renderer.Camera move function with the given key */
    public void move(int key) {
        camera.move(key);
        uploadViewMatrix();
    }
}

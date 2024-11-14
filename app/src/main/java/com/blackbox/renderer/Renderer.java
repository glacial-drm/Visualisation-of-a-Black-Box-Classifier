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

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private int sWidth, sHeight;
    private Camera camera;
    private final Set<Integer> moveKeys = Set.of(GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D);
    private HashMap<PrimitiveTypes, Shader> shaders = new HashMap<PrimitiveTypes, Shader>();
    private ArrayList<Primitive> objects = new ArrayList<Primitive>();

    /**
     * the main render loop
     * takes no data and returns no data
     * Handles clearing the screen, setting up buffers, creating shaders and rendering the science
     */
    public void init(){
        initShaders();
        uploadProjectionMatrix();
        uploadViewMatrix();
        loadJunkData();
    }

    public void render() {
        clearScreen();
        checkViewUpdate();

        for(Primitive obj : objects) {
            Matrix4f transformationMatrix = Camera.createTransformMatrix(obj.getPosition(), obj.getRotation(), obj.getScale());
            shaders.get(obj.getType()).loadTransformMatrix(transformationMatrix);
            obj.enableAttributes();
            glDrawElements(GL_TRIANGLES, obj.getIndexCount(), GL_UNSIGNED_INT, 0);
            obj.disable();
        }
    }

    // Function made until the sampler can pass through points
    private void loadJunkData(){
        Disk d = new Disk();
        d.create(new Vector3f(0, 0, -5), 0.5f, new Vector3f(0, 0, 0), 1f, Color.BLUE);
        objects.add(d);

        Disk d2 = new Disk();
        d2.create(new Vector3f(1.5f, 0, -5), 0.5f, new Vector3f(0, 0, 0), 1f, Color.RED);
        objects.add(d2);
    }

    private void checkViewUpdate() {
        if(camera.isViewChanged()){
            uploadViewMatrix();
            camera.resetViewChanged();
        }
    }

    private void uploadProjectionMatrix(){
        for (Shader shader : shaders.values()) {
            shader.loadProjectionMatrix(camera.getProjectionMatrix());
        }
    }

    private void uploadViewMatrix(){
        for (Shader shader : shaders.values()) {
            shader.loadViewMatrix(camera.getViewMatrix());
        }
    }

    private void initShaders(){
        // Load the disk shader
        Shader diskShader = new Shader();
        diskShader.create("vertex.glsl", "fragment.glsl");
        shaders.put(PrimitiveTypes.DISK, diskShader);
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

    public void move(int key) {
        camera.move(key);
    }
}

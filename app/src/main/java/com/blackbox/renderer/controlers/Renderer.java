package com.blackbox.renderer.controlers;

import com.blackbox.renderer.model.Model;
import com.blackbox.renderer.model.primitives.Point;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private final Model model;
    /**
     * Resizes the window to the given size <br>
     * initialise the shaders and creates there shader programs <br>
     * Uploads the projection and view matrix
     */
    public Renderer(Model model) {
        this.model = model;

        resize(model.getWidth(), model.getHeight());
        uploadProjectionMatrix();
        uploadViewMatrix();
    }

    /**
     * the main render loop
     * Handles clearing the screen, uploading uniforms and rendering objects
     */
    public void render() {
        clearScreen();
        move();

        model.prepPoints();
        for (Point obj : model.getObjects()) {
            obj.uploadUniforms(model.getCamera(), model.getShaders().get(obj.getClass()));
            obj.enableAttributes();
            glDrawElements(GL_TRIANGLES, obj.getIndexCount(), GL_UNSIGNED_INT, 0);
            obj.disable();
        }
    }

    /** Uploads the projection matrix to all the shaders */
    private void uploadProjectionMatrix() {
        for (Shader shader :  model.getShaders().values()) {
            if(shader.isWorldBased())
                shader.loadUniform("projectionMatrix", model.getCamera().getProjectionMatrix());
        }
    }

    /** Uploads the view matrix to all the shaders */
    private void uploadViewMatrix() {
        for (Shader shader : model.getShaders().values()) {
            if(shader.isWorldBased())
                shader.loadUniform("viewMatrix", model.getCamera().getViewMatrix());
        }
    }

    /**
     * Changes the viewport of the program
     *
     * @param width  must be less than the screen width
     * @param height must be less than the screen height
     */
    public void resize(int width, int height) {
        model.updateSize(width, height);

        glViewport(0, 0, width, height);

        Camera camera = model.getCamera();
        if(camera == null){
            model.setCamera( new Camera(width, height, 70, 0, 100));
        } else{
            camera.resize(width, height);
        }


        uploadProjectionMatrix();
    }

    /**
     * Handles clearing the opengl buffers and resting the screen to a default colour
     */
    private void clearScreen() {
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }


    /** Calls the {@link Camera move function with the given key */
    public void move() {
        if(model.getCamera().move()){
            uploadViewMatrix();
        }
    }
}

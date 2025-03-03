package com.blackbox.renderer.controler;

import com.blackbox.renderer.controllers.Renderer;
import com.blackbox.renderer.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.*;

public class RendererTest {
    Renderer renderer;

    @BeforeEach
    void init() {
        glfwInit();
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        Model model = new Model(300, 300);
        renderer = new Renderer(model);
    }

}

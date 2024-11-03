package com.blackbox;

import com.blackbox.graph.Canvas;
import com.blackbox.renderer.Renderer;
import com.blackbox.renderer.Shader;
import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.canvas.GLProfile;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;
import org.junit.jupiter.api.*;
import org.lwjgl.opengl.GL;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;

public class ShaderTest {
    Shader shader;

    @BeforeEach
    void init(){
        shader = new Shader();
        glfwInit();
        long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @DisplayName("create Shader")
    @Test
    void testCreateShader() {
        assertTrue(shader.create("vertex.glsl", "fragment.glsl"));
    }

    @DisplayName("create Shader missing vertex")
    @Test
    void testMissingVertex() {
        assertFalse(shader.create("", "fragment.glsl"));
    }
    @DisplayName("create Shader missing Fragment")
    @Test
    void testMissingFragment() {
        assertFalse(shader.create("vertex.glsl", ""));
    }

    @DisplayName("create Shader missing both")
    @Test
    void testMissingBoth() {
        assertFalse(shader.create("", ""));
    }

    @DisplayName("create Shader misspell file")
    @Test
    void testMissingSpellFile() {
        assertFalse(shader.create("veraex.glsl", "fragment.glsl"));
    }

    @DisplayName("Check setActive")
    @Test
    void checkSetActive() {
        shader.create("vertex.glsl", "fragment.glsl");
        shader.setActive();
    }
}

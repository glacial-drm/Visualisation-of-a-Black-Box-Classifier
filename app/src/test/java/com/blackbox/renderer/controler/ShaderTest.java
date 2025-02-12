package com.blackbox.renderer.controler;

import com.blackbox.renderer.controlers.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.glUseProgram;

class ShaderTest {

    private long window;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize GLFW and create a minimal window with an OpenGL context.
        glfwInit();
        window = glfwCreateWindow(1, 1, "Testing Shader", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        // Ensure no shader is bound.
        glUseProgram(0);
        // Create test shader files if they do not exist.
        createTestShaderFiles();
    }

    @AfterEach
    void cleanUp() {
        // Unbind any shader.
        glUseProgram(0);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    @AfterAll
    static void clean() throws IOException {
        Path vertexDir = Paths.get("src/main/shaders/vertex");
        Path fragmentDir = Paths.get("src/main/shaders/fragment");

        Path vertexShaderPath = vertexDir.resolve("testShader.vert");
        Path fragmentShaderPath = fragmentDir.resolve("testShader.frag");

        if(Files.exists(vertexShaderPath)) {
            Files.delete(vertexShaderPath);
        }

        if(Files.exists(fragmentShaderPath)) {
            Files.delete(fragmentShaderPath);
        }
    }

    /**
     * Helper method which creates minimal valid shader files in the expected locations.
     */
    private void createTestShaderFiles() throws IOException {
        Path vertexDir = Paths.get("src/main/shaders/vertex");
        Path fragmentDir = Paths.get("src/main/shaders/fragment");
        if (!Files.exists(vertexDir)) {
            Files.createDirectories(vertexDir);
        }
        if (!Files.exists(fragmentDir)) {
            Files.createDirectories(fragmentDir);
        }
        Path vertexShaderPath = vertexDir.resolve("testShader.vert");
        Path fragmentShaderPath = fragmentDir.resolve("testShader.frag");

        // Minimal vertex shader
        if (!Files.exists(vertexShaderPath)) {
            String vertexShaderSource = """
                    #version 330 core
                    layout(location = 0) in vec3 position;
                    uniform mat4 projection;
                    void main() {
                        gl_Position = projection * vec4(position, 1.0);
                    }
                    """;
            Files.writeString(vertexShaderPath, vertexShaderSource);
        }
        // Minimal fragment shader
        if (!Files.exists(fragmentShaderPath)) {
            String fragmentShaderSource = """
                    #version 330 core
                    out vec4 FragColor;
                    uniform vec3 color;
                    void main() {
                        FragColor = vec4(color, 1.0);
                    }
                    """;
            Files.writeString(fragmentShaderPath, fragmentShaderSource);
        }
    }

    // =========================================================================
    // Shader Creation Tests
    // =========================================================================

    @Test
    void testShaderCreationValid() {
        Shader shader = new Shader(true);
        boolean created = shader.create("testShader.vert", "testShader.frag");
        assertTrue(created, "Shader creation should return true for valid shader files");
        // After creation, no shader should be bound.
        int currentProgram = glGetInteger(GL_CURRENT_PROGRAM);
        assertEquals(0, currentProgram, "No shader should be bound after creation");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after shader creation");
    }

    @Test
    void testShaderCreationInvalidFiles() {
        Shader shader = new Shader(false);
        boolean created = shader.create("nonexistent.vert", "nonexistent.frag");
        assertFalse(created, "Shader creation should return false for invalid shader file names");
        // When creation fails, no shader should be bound.
        int currentProgram = glGetInteger(GL_CURRENT_PROGRAM);
        assertEquals(0, currentProgram, "No shader should be bound when creation fails");
        assertNotEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after failed shader creation");
    }

    // =========================================================================
    // Shader Bind/Unbind Tests
    // =========================================================================

    @Test
    void testShaderBindAndUnbind() {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        // Bind the shader and check that the current program is non-zero.
        shader.bind();
        int currentProgram = glGetInteger(GL_CURRENT_PROGRAM);
        assertNotEquals(0, currentProgram, "Shader should be bound and current program should be non-zero");
        // Unbind the shader and verify that the current program becomes 0.
        shader.unbind();
        currentProgram = glGetInteger(GL_CURRENT_PROGRAM);
        assertEquals(0, currentProgram, "Shader should be unbound and current program should be 0");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after bind/unbind cycle");
    }

    @Test
    void testShaderBindWhileAlreadyBound() {
        Shader shader1 = new Shader(true);
        Shader shader2 = new Shader(false);
        assertTrue(shader1.create("testShader.vert", "testShader.frag"), "First shader creation should succeed");
        assertTrue(shader2.create("testShader.vert", "testShader.frag"), "Second shader creation should succeed");
        shader1.bind();
        // Attempting to bind a second shader while one is already bound should throw an exception.
        assertThrows(IllegalStateException.class, shader2::bind, "Binding a shader while another is bound should throw an exception");
        shader1.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after unbinding");
    }

    @Test
    void testShaderUnbindWhenNotBound() {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        // Unbinding when the shader is not bound should throw an exception.
        assertThrows(IllegalStateException.class, shader::unbind, "Unbinding a shader that is not bound should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting unbind when not bound");
    }

    // =========================================================================
    // Uniform Loading Tests
    // =========================================================================

    @Test
    void testLoadUniformVectorWithoutBind() {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        Vector3f vector = new Vector3f(1.0f, 0.5f, 0.25f);
        // Calling loadUniform without binding should throw an exception.
        assertThrows(IllegalStateException.class, () -> shader.loadUniform("color", vector), "Loading uniform without binding should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after failed loadUniform(vector) call");
    }

    @Test
    void testLoadUniformMatrixWithoutBind() {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        Matrix4f matrix = new Matrix4f().identity();
        // Calling loadUniform for a matrix without binding should throw an exception.
        assertThrows(IllegalStateException.class, () -> shader.loadUniform("projection", matrix), "Loading uniform without binding should throw an exception");
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after failed loadUniform(matrix) call");
    }

    @Test
    void testLoadUniformVectorAfterBind() {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        shader.bind();
        Vector3f vector = new Vector3f(0.2f, 0.4f, 0.6f);
        // Loading the uniform after binding should work without throwing an exception.
        assertDoesNotThrow(() -> shader.loadUniform("color", vector), "Loading uniform vector after binding should not throw an exception");
        shader.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after loadUniform(vector) call");
    }

    @Test
    void testLoadUniformMatrixAfterBind() {
        Shader shader = new Shader(false);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        shader.bind();
        Matrix4f matrix = new Matrix4f().identity();
        // Loading the uniform matrix after binding should work without throwing an exception.
        assertDoesNotThrow(() -> shader.loadUniform("projection", matrix), "Loading uniform matrix after binding should not throw an exception");
        shader.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after loadUniform(matrix) call");
    }

    @Test
    void testLoadUniformNonExistent() throws NoSuchFieldException, IllegalAccessException {
        Shader shader = new Shader(true);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        shader.bind();
        Vector3f vector = new Vector3f(1.0f, 1.0f, 1.0f);
        // Loading a uniform that does not exist in the shader should not throw but will store -1.
        assertDoesNotThrow(() -> shader.loadUniform("nonexistent", vector), "Loading a nonexistent uniform should not throw, though it logs an error");
        // Use reflection to verify that the uniform location for "nonexistent" is stored as -1.
        Field field = Shader.class.getDeclaredField("uniformLocations");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") HashMap<String, Integer> uniformLocations = (HashMap<String, Integer>) field.get(shader);
        assertTrue(uniformLocations.containsKey("nonexistent"), "UniformLocations should contain the key for the nonexistent uniform");
        assertEquals(-1, uniformLocations.get("nonexistent").intValue(), "A nonexistent uniform should have a location of -1");
        shader.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after loadUniform(nonexistent) call");
    }

    @Test
    void testMultipleUniformLoadCalls() {
        Shader shader = new Shader(false);
        assertTrue(shader.create("testShader.vert", "testShader.frag"), "Shader creation should succeed");
        shader.bind();
        Vector3f vector1 = new Vector3f(0.1f, 0.2f, 0.3f);
        Vector3f vector2 = new Vector3f(0.4f, 0.5f, 0.6f);
        // First load stores the uniform location.
        shader.loadUniform("color", vector1);
        // Second load should reuse the stored location.
        shader.loadUniform("color", vector2);
        shader.unbind();
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after multiple loadUniform calls");
    }

    // =========================================================================
    // Other Functional Tests
    // =========================================================================

    @Test
    void testIsWorldBasedGetter() {
        Shader shaderWorld = new Shader(true);
        Shader shaderScreen = new Shader(false);
        assertTrue(shaderWorld.isWorldBased(), "Shader should report worldBased as true");
        assertFalse(shaderScreen.isWorldBased(), "Shader should report worldBased as false");
    }

    @Test
    void testShaderCreationInvalidShaderCode() throws IOException {
        // Create temporary shader files with invalid GLSL code.
        Path vertexDir = Paths.get("src/main/shaders/vertex");
        Path fragmentDir = Paths.get("src/main/shaders/fragment");
        Path invalidVertex = vertexDir.resolve("invalidShader.vert");
        Path invalidFragment = fragmentDir.resolve("invalidShader.frag");
        String invalidCode = "this is not valid shader code";
        Files.writeString(invalidVertex, invalidCode);
        Files.writeString(invalidFragment, invalidCode);

        Shader shader = new Shader(true);
        boolean created = shader.create("invalidShader.vert", "invalidShader.frag");
        // Expect creation to fail due to shader compilation/linking errors.
        assertFalse(created, "Shader creation should return false for invalid shader code");
        // Clean up temporary files.
        Files.deleteIfExists(invalidVertex);
        Files.deleteIfExists(invalidFragment);
        assertEquals(GL_NO_ERROR, glGetError(), "No OpenGL errors should be present after attempting creation with invalid shader code");
    }
}

package com.blackbox.renderer.controlers;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;

/**
 * Class which handles creating vertex and fragment shaders and wrapping them up in programs
 * Allows for uploading uniforms and setting active programs
 */
public class Shader {
    private int id;
    private final HashMap<String, Integer> uniformLocations = new HashMap<String, Integer>();

    private final boolean worldBased;

    public Shader(boolean worldBased) {
        this.worldBased = worldBased;
    }

    /**
     * Gets vertex and fragment code and then compiles them to openGL shaders
     *
     * @param vertexFileName   expected to be in the src/main/shaders/vertex folder
     * @param fragmentFileName expected to be in the src/main/shaders/fragment folder
     */
    public boolean create(String vertexFileName, String fragmentFileName) {
        ensureNonBound();
        int vertexShader = Integer.MAX_VALUE, fragmentShader = Integer.MAX_VALUE;
        boolean validShaders = true, validProgram = false;

        try {
            vertexShader = createShader("vertex/" + vertexFileName, GL_VERTEX_SHADER);
            fragmentShader = createShader("fragment/" + fragmentFileName, GL_FRAGMENT_SHADER);
        } catch (IOException e) {
            System.err.println("Could not compile shader: " + e.getMessage());
            validShaders = false;
        }

        // Create the shader program and attach the shaders
        id = glCreateProgram();
        glAttachShader(id, vertexShader);
        glAttachShader(id, fragmentShader);
        glLinkProgram(id);

        boolean validLink = checkLinkStatus(id);

        glValidateProgram(id);
        validProgram = checkProgramStatus(id);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return validProgram && validLink && validShaders;
    }

    /**
     * Set this shader program as the one of OpenGL to use
     */
    public void bind() {
        ensureNonBound();
        glUseProgram(id);
    }

    public void unbind() {
        ensureBound();
        glUseProgram(0);
    }

    /**
     * Loads a vector 3 into GPU memory
     * @param name uniform name in the shader
     * @param vector a 3d vector of floating point values
     */
    public void loadUniform(String name, Vector3f vector) {
        ensureBound();
        checkUniformLocation(name);
        glUniform3f(uniformLocations.get(name), vector.x, vector.y, vector.z);
    }

    /**
     * Loads a 4x4 Matrix of floats into GPU memory
     * @param name uniform name in the shader
     * @param matrix 4x4 matrix of floats
     */
    public void loadUniform(String name, Matrix4f matrix) {
        ensureBound();
        checkUniformLocation(name);
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        glUniformMatrix4fv(uniformLocations.get(name), false, matrix.get(buf));
    }

    /**
     * Sets the shader to active and then checks if the uniformLocation is know
     * if not then it gets it from the GPU
     * @param name uniform name in shader
     */
    private void checkUniformLocation(String name) {
        ensureBound();
        if(!uniformLocations.containsKey(name)) {
            getUniformLocations(name);
        }
    }

    /**
     *  Gets the uniform location from the shader <br>
     *  will print to std.err if name is not found
     * @param name uniform name in shader
     */
    private void getUniformLocations(String name) {
        ensureBound();
        int location = glGetUniformLocation(id, name);

        if(location == -1) {
            System.err.println("Could not find uniform location: " + name);
        }

        uniformLocations.put(name, location);
    }

    /**
     * Checks if the shader compiled correctly
     * @param shader shader ID
     */
    private void checkShader(int shader) {
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE){
            System.err.println("[RENDERER] Could not link shader program: " + glGetShaderInfoLog(shader));
        }
    }

    /**
     * Checks the status of the program
     * @param program program ID
     * @return true if successful false if error
     */
    boolean checkProgramStatus(int program) {
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println("Validation error: " + glGetProgramInfoLog(program));
            return false;
        }
        return true;
    }

    /**
     * Checks the status of the linking of shaders to the program
     * @param program program ID
     * @return true if successful false if error
     */
    boolean checkLinkStatus(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Link error: " + glGetProgramInfoLog(program));
            return false;
        }
        return true;
    }

    /**
     * Reads in a file and returns its contents as a String
     *
     * @return file contents with new lines
     * @throws IOException if the file fails to read
     */
    private String readFile(String fileName) throws IOException {
        String content;

        Path filePath = Paths.get("src/main/shaders/" + fileName);

        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new IOException("Failed to read shader with file name: " + e.getMessage());
        }

        return content;
    }

    /**
     * Function which calls for the reading of the shader, creates the shader and checks for errors
     *
     * @param fileName   file type and path
     * @param shaderType type of shader EG GL_VERTEX_SHADER, GL_FRAGMENT_SHADER
     * @return int with openGL id for the shader
     * @throws IOException if the file fails to read or the shader compilation fails
     */
    private int createShader(String fileName, int shaderType) throws IOException {
        String shaderCode = readFile(fileName);

        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        checkShader(shader);

        return shader;
    }

    // -------------------------------------------------------------------------
    // OpenGL State Check Methods
    // -------------------------------------------------------------------------

    /**
     * Ensures that this VertexArray object is currently bound.
     *
     * @throws IllegalStateException if this VAO is not bound.
     */
    private void ensureBound() {
        int current = glGetInteger(GL_CURRENT_PROGRAM);;
        if (current != id) {
            throw new IllegalStateException("Shader " + id + " is not bound. Current bound shader is " + current);
        }
    }

    /**
     * Ensures that no VertexArray object is currently bound.
     *
     * @throws IllegalStateException if another VAO is bound.
     */
    private void ensureNonBound() {
        int current = glGetInteger(GL_CURRENT_PROGRAM);
        if (current != 0) {
            throw new IllegalStateException("Shader " + current + " is bound. No shader should be bound");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public boolean isWorldBased() {
        return worldBased;
    }
}

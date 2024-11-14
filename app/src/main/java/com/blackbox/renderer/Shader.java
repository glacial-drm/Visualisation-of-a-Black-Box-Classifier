package com.blackbox.renderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

/**
 * Class which handles creating vertex and fragment shaders and wrapping them up in programs
 * Allows for uploading uniforms and setting active programs
 */
public class Shader {
    private int id;

    // TODO : Make this an abstract class and have child classes for each shader
    private int transformMatrixLocation;
    private int projectionMatrixLocation;
    private int viewMatrixLocation;

    /**
     * Gets vertex and fragment code and then compiles them to openGL shaders
     *
     * @param vertexFileName   expected to be in the src/main/shaders/vertex folder
     * @param fragmentFileName expected to be in the src/main/shaders/fragment folder
     */
    public boolean create(String vertexFileName, String fragmentFileName) {
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

        // Check for errors
        try {
            validProgram = checkShaderProgram(id);
        } catch (IOException e) {
            System.err.println("Could not link program: " + e.getMessage());
        }

        if (!validProgram) {
            System.out.println("Could not link program but no error: " + id);
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        getUniformLocations();

        return validProgram && validShaders;
    }

    /**
     * Set this shader program as the one of OpenGL to use
     */
    public void setActive() {
        glUseProgram(id);
    }

    // Functions that handle sending data to the GPU
    public void loadTransformMatrix(Matrix4f matrix){
        loadMatrixUniform(matrix, transformMatrixLocation);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        loadMatrixUniform(matrix, projectionMatrixLocation);
    }

    public void loadViewMatrix(Matrix4f matrix){
        loadMatrixUniform(matrix, viewMatrixLocation);
    }

    private void loadMatrixUniform(Matrix4f matrix, int loc) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        glUniformMatrix4fv(loc, false, matrix.get(buf));
    }

    private void getUniformLocations(){
        transformMatrixLocation = glGetUniformLocation(id, "transformMatrix");
        projectionMatrixLocation = glGetUniformLocation(id, "projectionMatrix");
        viewMatrixLocation = glGetUniformLocation(id, "viewMatrix");
    }

    /**
     * Checks if the shader compiled correctly
     *
     * @param shader shader ID
     * @return true if successful
     * @throws IOException if the shader compilation failed
     */
    private Boolean checkShader(int shader) throws IOException {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        glGetShaderiv(shader, GL_COMPILE_STATUS, buf);

        if (buf.get() <= 0) {
            throw new RuntimeException("Failed to compile shader with ID: " + shader);
        }
        return true;
    }

    /**
     * Simple function to get the link status of the program
     *
     * @param program takes in the program id
     * @return true if successful
     * @throws IOException if the program fails to link
     */
    private boolean checkShaderProgram(int program) throws IOException {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        glGetShaderiv(program, GL_LINK_STATUS, buf);

        if (buf.get() != 0) {
            throw new RuntimeException("Failed to link program with ID: " + program);
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
        String content = "";

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

        Boolean compileSuc = checkShader(shader);

        return shader;
    }
}

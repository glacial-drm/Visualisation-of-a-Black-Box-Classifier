package com.blackbox.renderer;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int id;

    /**
     * Gets vertex and fragment code and then compiles them to openGL shaders
     * @param vertexFileName expected to be in the src/main/shaders/vertex folder
     * @param fragmentFileName expected to be in the src/main/shaders/fragment folder
     */
    public void create(String vertexFileName, String fragmentFileName) {
        int vertexShader = Integer.MAX_VALUE, fragmentShader = Integer.MAX_VALUE;
        try {
            vertexShader = createShader("vertex/" + vertexFileName, GL_VERTEX_SHADER);
            fragmentShader = createShader("fragment/" + fragmentFileName, GL_FRAGMENT_SHADER);
        } catch (IOException e) {
            System.err.println("Could not compile shader: " + e.getMessage());
        }

        // Create the shader program and attach the shaders
        id = glCreateProgram();
        glAttachShader(id, vertexShader);
        glAttachShader(id, fragmentShader);
        glLinkProgram(id);

        // Check for errors
        try {
            checkShaderProgram(id);
        } catch (IOException e) {
            System.err.println("Could not link program: " + e.getMessage());
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void setActive(){
        glUseProgram(id);
    }

    // Functions that handle sending data to the GPU

    public int getId(){
        return id;
    }

    /**
     * Reads in a file and returns its contents as a String
     * @throws IOException if the file fails to read
     * @return file contents with new lines
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

    private Boolean checkShader(int shader) throws IOException {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        glGetShaderiv(shader, GL_COMPILE_STATUS, buf);

        if (buf.get() <= 0) {
            throw new RuntimeException("Failed to compile shader with ID: " + shader);
        }
        return true;
    }

    private boolean checkShaderProgram(int program) throws IOException {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        glGetShaderiv(program, GL_LINK_STATUS, buf);

        if (buf.get() != 0) {
            throw new RuntimeException("Failed to link program with ID: " + program);
        }
        return true;
    }

    private int createShader(String fileName, int shaderType) throws IOException {
        String shaderCode = readFile(fileName);

        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        Boolean compileSuc = checkShader(shader);

        return shader;
    }
}

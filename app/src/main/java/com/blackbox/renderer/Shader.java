package com.blackbox.renderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Shader {
    private int id;

    /**
     * Gets vertex and fragment code and then compiles them to openGL shaders
     * @param vertexFileName expected to be in the src/main/shaders/vertex folder
     * @param fragmentFileName expected to be in the src/main/shaders/fragment folder
     */
    public void create(String vertexFileName, String fragmentFileName) {
        String vertexCode = readFile("vertex/" + vertexFileName);
        String fragmentCode = readFile("fragment/" + fragmentFileName);
    }

    public void setActive(){

    }

    // Functions that handle sending data to the GPU

    public int getId(){
        return id;
    }

    /**
     * Reads in a file and returns its contents as a String
     * @throws NA does not throw an error even if the file could not be read due to the program still being
     * able to function without the shader HOWEVER this is UB and should be avoided
     * @return file contents with new lines
     */
    private String readFile(String fileName) {
        String content = "";

        Path filePath = Paths.get("src/main/shaders/" + fileName);

        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            // Do not throw as the program will probably be able to function however this is undefined behaviour
            System.err.println("Failed to read the file: " + e.getMessage());
        }

        return content;
    }
}

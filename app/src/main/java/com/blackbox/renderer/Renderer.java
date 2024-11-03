package com.blackbox.renderer;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private int sWidth, sHeight;

    /**
     * the main render loop
     * takes no data and returns no data
     * Handles clearing the screen, setting up buffers, creating shaders and rendering the science
     */
    public void render(){
        clearScreen();

        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f,
        };

        String vertexCode = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                
                void main()
                {
                gl_Position = aPos;
                }
                """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexCode);
        glCompileShader(vertexShader);

        String fragmentCode = """
                #version 330 core
                out vec4 FragColor;
                in vec3 colour;
                void main() {
                    FragColour = (1.0f, 0.0f, 0.0f, 1.0f);
                }""";

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentCode);
        glCompileShader(fragmentShader);

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        Vao vao = new Vao();
        vao.create();
        vao.bind();

        Vbo vbo = new Vbo();
        vbo.create();
        vbo.storeFloatData(vertices, 0);
        vao.enableVertexAttrib(0);

        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    /**
     * Changes the viewport of the program
     * @param width must be less than the screen width
     * @param height must be less than the screen height
     */
    public void resize(int width, int height){
        // Could check args are less than screen size
        sWidth = width;
        sHeight = height;

        glViewport(0, 0, sWidth, sHeight);
    }

    /**
     * Handles clearing the opengl buffers and resting the screen to a default colour
     */
    private void clearScreen() {
        glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}

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

        Shader shader = new Shader();
        shader.create("vertex.glsl", "fragment.glsl");


        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f,
        };
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        String vertexCode = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                out vec3 colour;
                void main()
                {
                gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                colour = aPos
                }
                """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexCode);
        glCompileShader(vertexShader);

        String fragmentCode = """
                #version 330 core
                out vec4 FragColor;
                in vec3 colour;
                void main() {\
                    FragColor = vec4(colour, 1.0f);\
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

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

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

package com.blackbox.graph;

import javafx.scene.layout.Region;

import static org.lwjgl.opengl.GL11.*;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.canvas.GLCanvasAnimator;
import com.huskerdev.openglfx.canvas.GLProfile;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

public class Canvas {
    public static Region createCanvas() {
        GLCanvas canvas = new GLCanvas(
            LWJGLExecutor.LWJGL_MODULE,
            GLProfile.Core,
            // flipY
            false,
            // msaa
            0,
            // fxaa
            false
        );
        canvas.setAnimator(new GLCanvasAnimator(60.0));

        canvas.addOnInitEvent(event -> {
            System.out.println("init");
        });
        canvas.addOnReshapeEvent(event -> {
            System.out.println("reshape");
        });
        canvas.addOnRenderEvent(event -> {
            glClearColor(255f, (float)Math.random() % 255f, 0f, 1f);

            // clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        });
        canvas.addOnDisposeEvent(event -> {
            System.out.println("dispose");
        });

        return canvas;
    }
}

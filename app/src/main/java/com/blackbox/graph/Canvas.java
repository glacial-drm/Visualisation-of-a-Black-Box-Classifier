package com.blackbox.graph;

import javafx.scene.layout.Region;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.PointsLoaded;
import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.canvas.GLCanvasAnimator;
import com.huskerdev.openglfx.canvas.GLProfile;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

public class Canvas {
    /**
     * Create a {@link com.huskerdev.openglfx.canvas.GLCanvas GLCanvas}.
     *
     * @return: {@link javafx.scene.layout.Region Region}
     *          to abstract away {@link com.huskerdev.openglfx.canvas.GLCanvas GLCanvas}
     */
    public static Region createCanvas(ConcurrentLinkedQueue<CanvasEvent> eventQueue) {
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
            // Check event queue
            while (!eventQueue.isEmpty()) {
                // Get an event
                CanvasEvent canvasEvent = eventQueue.poll();
                if (canvasEvent == null) { break; }

                // Process event
                if (canvasEvent instanceof PointsLoaded) {
                    PointsLoaded pointsLoaded = (PointsLoaded)canvasEvent;
                    System.out.println(String.format("Received PointsLoaded event with data: %d", pointsLoaded.data));
                }
            }

            // Do rendering
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

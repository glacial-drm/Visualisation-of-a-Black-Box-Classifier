package com.blackbox.graph;

import com.blackbox.renderer.Renderer;
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

        Renderer renderer = new Renderer();
        canvas.addOnInitEvent(event -> {
            System.out.println("init");
        });
        canvas.addOnReshapeEvent(event -> {
            renderer.resize(event.width, event.height);
        });
        canvas.addOnRenderEvent(event -> {
            // Check event queue
            while (!eventQueue.isEmpty()) {
                // Get an event
                CanvasEvent canvasEvent = eventQueue.poll();
                if (canvasEvent == null) {
                    break;
                }

                // Process event
                if (canvasEvent instanceof PointsLoaded) {
                    PointsLoaded pointsLoaded = (PointsLoaded) canvasEvent;
                    System.out.println(String.format("Received PointsLoaded event with data: %d", pointsLoaded.data));
                }
            }

            // Do rendering
            renderer.render();

        });
        canvas.addOnDisposeEvent(event -> {
            System.out.println("dispose");
        });

        return canvas;
    }
}

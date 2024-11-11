package com.blackbox.graph;

import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.*;
import com.blackbox.renderer.Renderer;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The graph container, handles GLFW, the window, user input, and the renderer.
 *
 * Should only be used with the static {@link com.blackbox.graph.Canvas#run Canvas.run} method.
 * This ensures all operations are run on a single thread, which is required for GLFW.
 */
public class Canvas {
    private long window;
    private final Renderer renderer = new Renderer();
    private final ConcurrentLinkedQueue<CanvasEvent> eventQueue;
    private boolean running = true;

    private Canvas(ConcurrentLinkedQueue<CanvasEvent> eventQueue) {
        this.eventQueue = eventQueue;

        init();
        registerInput();
    }

    /**
     * Initialise the canvas.
     *
     * This sets up GLFW and opens the window.
     */
    private void init() {
        // Setup logger
        GLFWErrorCallback.createPrint(System.err).set();

        // Setup GLFW
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialise GLFW");
        }

        // Create window

        // Hide window until its shown
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        window = glfwCreateWindow(300, 300, "Hello world", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);
        // Enable V-Sync
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glfwSetWindowSizeCallback(window, (_window, width, height) -> {
            renderer.resize(width, height);
        });
    }

    /**
     * Register callbacks to respond to user input.
     */
    private void registerInput() {
        glfwSetKeyCallback(window, (_window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
    }

    /**
     * Whether the app is currently running and should continue looping.
     */
    private boolean isRunning() {
        if (running) {
            running = !glfwWindowShouldClose(window);
            return running;
        } else {
            return running;
        }
    }

    /**
     * The main application loop.
     *
     * Does the rendering and responds to events.
     */
    private void loop() {
        renderer.render();

        glfwSwapBuffers(window);
        glfwPollEvents();

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
            } else if (canvasEvent instanceof StopEvent) {
                running = false;
            }
        }
    }

    /**
     * Cleanup LWJGL and GLFW.
     */
    private void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        // Stop all threads
        System.exit(0);
    }

    /**
     * Run the Canvas in a new thread.
     *
     * @param eventQueue To pass events into the created thread.
     */
    public static Thread run(ConcurrentLinkedQueue<CanvasEvent> eventQueue) {
        Thread thread = new Thread(() -> {
            Canvas canvas = new Canvas(eventQueue);

            while (canvas.isRunning()) {
                canvas.loop();
            }

            canvas.destroy();
        });

        thread.start();

        return thread;
    }
}

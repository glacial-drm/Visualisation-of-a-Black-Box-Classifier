package com.blackbox.graph;

import com.blackbox.graph.Input.InputHandler;
import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.*;
import com.blackbox.renderer.Renderer;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL33.*;

/**
 * The graph container, handles GLFW, the window, user input, and the renderer.
 *
 * Should only be used with the static {@link com.blackbox.graph.Canvas#run Canvas.run} method.
 * This ensures all operations are run on a single thread, which is required for GLFW.
 */
public class Canvas {
    private long window;
    private Renderer renderer;
    private final ConcurrentLinkedQueue<CanvasEvent> eventQueue;

    /** Whether to keep running. */
    private boolean running = true;
    /**
     * The stop event given when quitting.
     * Stored as it has options about how to quit.
     */
    private StopEvent stopEvent = null;

    private final int width = 300;
    private final int height = 300;

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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);


        // Hide window until its shown
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, "Hello world", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);
        // Enable V-Sync
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer = new Renderer(width, height);

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
            } else if(action == GLFW_PRESS) {
                InputHandler.keyDown((char)key);
            } else if (action == GLFW_RELEASE) {
                InputHandler.keyUp((char)key);
            }
        });

        glfwSetMouseButtonCallback(window, (_window, button, action, mods) -> {
            if(action == GLFW_PRESS) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                InputHandler.mouseDown(button);
            } else if (action == GLFW_RELEASE) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                InputHandler.mouseUp(button);
            }
        });

        glfwSetCursorPosCallback(window, (_window, xpos, ypos) -> {
            InputHandler.setMouseX(xpos);
            InputHandler.setMouseY(ypos);
        });

        glfwSetScrollCallback(window, (_window, xOffSet, yOffSet) -> {
            InputHandler.incXOffSet(xOffSet);
            InputHandler.incZoom(yOffSet);
        });
    }

    /**
     * Whether the app is currently running and should continue looping.
     */
    private boolean isRunning() {
        if (running && glfwWindowShouldClose(window)) {
            running = false;
            stopEvent = new StopEvent(true);
        }

        return running;
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
            if (canvasEvent instanceof PointsLoaded pointsLoaded) {
                System.out.println(String.format(
                    "Received PointsLoaded event: %f %f %d",
                    pointsLoaded.point.feature1,
                    pointsLoaded.point.feature2,
                    pointsLoaded.point.label
                ));
            } else if (canvasEvent instanceof StopEvent event) {
                running = false;
                stopEvent = event;
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

        if (stopEvent.quitAllThreads) {
            // Stop all threads
            System.exit(0);
        }
    }

    /**
     * Run the Canvas, taking over this thread.
     * Will quit all other threads when quitting.
     *
     * @param eventQueue To pass events into the created thread.
     */
    public static void run(ConcurrentLinkedQueue<CanvasEvent> eventQueue) {
        Canvas canvas = new Canvas(eventQueue);

        while (canvas.isRunning()) {
            canvas.loop();
        }

        canvas.destroy();
    }
}

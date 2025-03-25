package com.blackbox.graph.Input;

import java.util.HashMap;

public class InputHandler {
    private static final HashMap<Character, Boolean> keys = new HashMap<>();
    private static final HashMap<Integer, Boolean> mouseInput = new HashMap<>();
    private static double mouseX, mouseY;
    private static double originalMouseX, originalMouseY;
    private static double xOffSet;
    private static double zoom = -10;

    public static double getMouseX() {
        return mouseX;
    }

    public static void setMouseX(double mouseX) {
        InputHandler.mouseX = mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static void setMouseY(double mouseY) {
        InputHandler.mouseY = mouseY;
    }

    public static void keyDown(char key) {
        keys.put(key, true);
    }

    public static void keyUp(char key) {
        keys.put(key, false);
    }

    public static boolean getKeyState(char key) {
        Boolean value = keys.get(key);
        return keys.get(key) != null && value;
    }

    public static void mouseDown(int code) {
        setOriginalMouseX(mouseX);
        setOriginalMouseY(mouseY);

        mouseInput.put(code, true);
    }

    public static void mouseUp(int code) {
        mouseInput.put(code, false);
    }

    public static boolean getMouseState(MouseInputNames input) {
        int code = MouseInputNames.convertMouseToCode(input);

        Boolean value = mouseInput.get(code);
        return mouseInput.get(code) != null && value;
    }

    public static double getOriginalMouseX() {
        return originalMouseX;
    }

    public static void setOriginalMouseX(double originalMouseX) {
        InputHandler.originalMouseX = originalMouseX;
    }

    public static double getOriginalMouseY() {
        return originalMouseY;
    }

    public static void setOriginalMouseY(double originalMouseY) {
        InputHandler.originalMouseY = originalMouseY;
    }

    public static void incZoom(double yOffSet) {
        zoom += yOffSet;
        zoom = Math.min(zoom, -0.15);
    }

    public static void incXOffSet(double xOffSet) {
        InputHandler.xOffSet += xOffSet;
    }

    public static double getZoom() {
        // Account for the camera being perspective for now
        // Speeds up zooming further away from the origin
        // position.z = -(float)Math.pow(-zoom, 1.5) * 0.8f;
        return -Math.pow(-InputHandler.zoom, 1.5) * 0.2;
    }

    public static float inverseZoom() {
        return (float)Math.pow((float)Math.abs(InputHandler.getZoom()) / 0.2f, (float)(2.0f/3.0f));
    }
}

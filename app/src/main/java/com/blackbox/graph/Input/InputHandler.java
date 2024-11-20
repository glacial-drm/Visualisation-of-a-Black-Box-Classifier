package com.blackbox.graph.Input;

import java.util.HashMap;

public class InputHandler {
    private static final HashMap<Character, Boolean> keys = new HashMap<>();
    private static final HashMap<Integer, Boolean> mouseInput = new HashMap<>();

    public static void keyDown(char key){
        keys.put(key, true);
    }

    public static void keyUp(char key){
        keys.put(key, false);
    }

    public static boolean getKeyState(char key){
        Boolean value = keys.get(key);
        return keys.get(key) != null && value;
    }

    public static void mouseDown(int code){
        mouseInput.put(code, true);
    }

    public static void mouseUp(int code){
        mouseInput.put(code, false);
    }

    private static int convertMouseToCode(MouseInputNames input){
        return switch (input) {
            case MouseInputNames.LEFT_CLICK -> 0;
            case MouseInputNames.RIGHT_CLICK -> 1;
            case MouseInputNames.MIDDLE_CLICK -> 2;
        };
    }

    public static boolean getMouseState(MouseInputNames input){
        int code = InputHandler.convertMouseToCode(input);

        Boolean value = mouseInput.get(code);
        return mouseInput.get(code) != null && value;
    }
}

package com.blackbox.graph;

import java.util.HashMap;

public class InputHandler {
    private static final HashMap<Character, Boolean> keys = new HashMap<>();
    private static final HashMap<Character, Boolean> mouses = new HashMap<>();

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
}

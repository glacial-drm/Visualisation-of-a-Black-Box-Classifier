package com.blackbox.graph.Input;

public enum MouseInputNames {
    LEFT_CLICK, RIGHT_CLICK, MIDDLE_CLICK;

    public static int convertMouseToCode(MouseInputNames input) {
        return switch (input) {
            case MouseInputNames.LEFT_CLICK -> 0;
            case MouseInputNames.RIGHT_CLICK -> 1;
            case MouseInputNames.MIDDLE_CLICK -> 2;
        };
    }
}

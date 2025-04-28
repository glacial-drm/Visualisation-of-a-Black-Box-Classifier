package com.blackbox.app.events;

public class ErrorDialog extends ControllerEvent {
    public final String message;

    public ErrorDialog(String message) {
        this.message = message;
    }
}

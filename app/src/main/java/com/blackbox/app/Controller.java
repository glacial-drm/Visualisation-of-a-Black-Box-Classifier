package com.blackbox.app;

public class Controller {
    /** The model. */
    private Model model;

    Controller(Model model) {
        this.model = model;
    }

    /** Get the message. */
    public String getMesssge() {
        return model.message;
    }
}

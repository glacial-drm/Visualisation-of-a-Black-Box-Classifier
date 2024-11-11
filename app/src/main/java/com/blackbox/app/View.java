package com.blackbox.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View extends Application {
    /** The controller. */
    static Controller controller;

    @Override
    public void start(Stage primaryStage) {
        // Test title
        primaryStage.setTitle(controller.getMesssge());

        // Test button to load points
        Button button = new Button("PointsLoaded");
        button.onActionProperty().set(event -> {
            controller.pointsLoaded(3);
        });

        // Container
        VBox container = new VBox(button);
        container.setPadding(new Insets(8));
        container.setSpacing(16);

        // Setup
        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }
}

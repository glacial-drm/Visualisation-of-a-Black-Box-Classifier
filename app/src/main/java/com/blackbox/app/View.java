package com.blackbox.app;

import com.blackbox.graph.Canvas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View extends Application {
    /** The controller. */
    static Controller controller;

    @Override
    public void start(Stage primaryStage) {
        // Test title
        primaryStage.setTitle(controller.getMesssge());

        // Canvas
        Region canvas = Canvas.createCanvas(controller.eventQueue);
        canvas.setMinWidth(200);
        canvas.setMaxWidth(200);
        canvas.setMinHeight(200);
        canvas.setMaxHeight(200);

        // Test button to load points
        Button button = new Button("PointsLoaded");
        button.onActionProperty().set(event -> {
            controller.pointsLoaded(3);
        });

        // Container
        VBox container = new VBox(canvas, button);
        container.setPadding(new Insets(8));
        container.setSpacing(16);

        // Setup
        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }
}

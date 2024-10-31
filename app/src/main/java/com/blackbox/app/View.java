package com.blackbox.app;

import com.blackbox.graph.Canvas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View extends Application {
    /** The controller. */
    static Controller controller;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(controller.getMesssge());

        Label label1 = new Label(controller.getMesssge());
        Label label2 = new Label(controller.getMesssge());

        Region canvas = Canvas.createCanvas();
        canvas.setMinWidth(200);
        canvas.setMaxWidth(200);
        canvas.setMinHeight(200);
        canvas.setMaxHeight(200);

        VBox container = new VBox(label1, canvas, label2);
        container.paddingProperty().set(new Insets(8));

        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }
}

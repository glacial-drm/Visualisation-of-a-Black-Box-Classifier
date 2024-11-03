package com.blackbox.app;

import com.blackbox.graph.Canvas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class View extends Application {
    /** The controller. */
    static Controller controller;

    @Override
    public void start(Stage primaryStage) {
        // Test title
        primaryStage.setTitle(controller.getMesssge());

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        for(int i = 0; i < controller.getMenuList().length; i++){
            menuBar.getMenus().add(i, new Menu(controller.getMenuList()[i]));
        }

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

        // Classifier Section
        Region classifierRegion = new Region();

        // Output Details Section
        Region outputRegion = new Region();

        // Navigation Details Section
        Region navigationRegion = new Region();

        // Container --change to gridpane(?)
        VBox container = new VBox(menuBar, canvas, button);
        container.setPadding(new Insets(8));
        container.setSpacing(16);

        // Setup
        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }
}

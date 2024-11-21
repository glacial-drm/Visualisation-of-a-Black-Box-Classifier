package com.blackbox.app;

import com.blackbox.graph.Canvas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application {
    /** The controller. */
    static Controller controller;

    @Override
    public void start(Stage primaryStage) {
        // Test title
        primaryStage.setTitle(controller.getMessage());

        // Menu Bar
        MenuBar menuBar = new MenuBar();
        for(int i = 0; i < controller.getMenuList().length; i++){
            menuBar.getMenus().add(i, new Menu(controller.getMenuList()[i]));
        }

        // "File" Menu
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Classifier");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ONNX Files", "*.ONNX")
        );

        for(int i = 0; i < controller.getFileList().length; i++){
            menuBar.getMenus().get(0).getItems().add(i, new MenuItem(controller.getFileList()[i]));
        }
        menuBar.getMenus().get(0).getItems().get(0).setOnAction(e -> {
            File newClassifier = fc.showOpenDialog(primaryStage);
            // Either use name taken from imported file, or have user write new name (with character limit)
            // Write it to the dropdown to store it for later usage (during the same instance of the program)
        });


        // Canvas (Graph section)
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
        GridPane classifierRegion = new GridPane();
        ComboBox<Text> classifierList = new ComboBox<Text>();

        classifierRegion.add(new Text("Classifier Details:"), 0, 0);
        classifierRegion.add(new Text("Chosen Classifier: "), 0, 1);
        classifierRegion.add(classifierList, 1, 1);

        classifierRegion.setMinWidth(200);
        classifierRegion.setMaxWidth(200);
        classifierRegion.setMinHeight(200);
        classifierRegion.setMaxHeight(200);

        // Output Details Section
        GridPane outputRegion = new GridPane();


        outputRegion.add(new Text("Output Details:"), 0, 0);

        outputRegion.setMinWidth(200);
        outputRegion.setMaxWidth(200);
        outputRegion.setMinHeight(200);
        outputRegion.setMaxHeight(200);

        // Navigation Details Section
        GridPane navigationRegion = new GridPane();


        navigationRegion.add(new Text("Navigation Details:"), 0, 0);

        navigationRegion.setMinWidth(200);
        navigationRegion.setMaxWidth(200);
        navigationRegion.setMinHeight(200);
        navigationRegion.setMaxHeight(200);

        // Adding sections to grid
        GridPane grid = new GridPane();
        grid.add(classifierRegion, 0, 0);
        grid.add(outputRegion, 1, 0);
        grid.add(navigationRegion, 2, 0);

        // Container
        VBox container = new VBox(menuBar, grid);
        container.setPadding(new Insets(8));
        container.setSpacing(16);

        // Setup
        primaryStage.setScene(new Scene(container));
        primaryStage.show();
    }
}

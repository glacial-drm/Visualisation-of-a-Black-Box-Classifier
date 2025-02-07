package com.blackbox.app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class View extends Application {
  /**
   * The controller.
   */
  static Controller controller;
  private VBox classifierBox;

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("UON Blackbox Project");

    // Main layout
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: #2c2c2c;");

    // Left Pane
    VBox leftPane = new VBox();
    leftPane.setAlignment(Pos.TOP_CENTER);
    leftPane.setPrefWidth(200);
    leftPane.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 0;");

    // Folder Name ()
    Label folderLabel = new Label("Folder Name");
    folderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    folderLabel.setTextFill(Color.PURPLE);
    folderLabel.setStyle(" -fx-padding: 20;");

    // Classifiers
    classifierBox = new VBox(15);
    classifierBox.setAlignment(Pos.TOP_CENTER);
    classifierBox.setStyle("-fx-padding: 20 0 20 0;");

    List<String> classifiers = controller.getClassifiers();
    updateClassifierBox(classifierBox, classifiers);

    // Set active model
    Button setButton = new Button("Set");
    setButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    setButton.setTextFill(Color.PURPLE);
    setButton.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: transparent;");
    setButton.setMaxWidth(Double.MAX_VALUE);
    setButton.setPrefHeight(50);
    setButton.setOnAction(event -> {
      // Update Right Part
      String selectedClassifier = controller.getSelectedClassifier();
      if (selectedClassifier != null) {
        updateRightPane(root, selectedClassifier);
      } else {
        updateRightPane(root, "No classifier selected");
      }
    });


    Button loadButton = new Button("Load");
    loadButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    loadButton.setTextFill(Color.PURPLE);
    loadButton.setStyle("-fx-background-color: #2c2c2c; -fx-border-color: transparent;");
    loadButton.setMaxWidth(Double.MAX_VALUE);
    loadButton.setPrefHeight(50);
    loadButton.setOnAction(event -> {
      DirectoryChooser chooser = controller.getFileDialog();
      File selectedDirectory = chooser.showDialog(primaryStage);
      controller.loadAllOnnx(selectedDirectory);
      updateClassifiers();
    });

    VBox.setVgrow(classifierBox, Priority.ALWAYS);
    updateRightPane(root, "No classifier selected");
    leftPane.getChildren().addAll(folderLabel, classifierBox, setButton, loadButton);


    // Set layout
    root.setLeft(leftPane);

    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void updateClassifierBox(VBox classifierBox, List<String> classifiers) {
    classifierBox.getChildren().clear();
    if (classifiers.isEmpty()) {
      Label emptyLabel = new Label("No classifiers available");
      emptyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
      emptyLabel.setTextFill(Color.GRAY);
      classifierBox.getChildren().add(emptyLabel);
    } else {
      for (String classifier : classifiers) {
        Label classifierLabel = new Label(classifier);
        classifierLabel.setMaxWidth(Double.MAX_VALUE);
        classifierLabel.setPrefHeight(40);
        classifierLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        classifierLabel.setTextFill(Color.PURPLE);
        classifierLabel.setAlignment(Pos.CENTER);

        // While selected, background color become same as right
        String selectedClassifier = controller.getSelectedClassifier();
        if (classifier.equals(selectedClassifier)) {
          classifierLabel.setStyle("-fx-background-color: #2c2c2c;");

        } else {
          classifierLabel.setStyle("-fx-background-color: #1e1e1e;");
        }

        classifierLabel.setOnMouseClicked(event -> {
          controller.setSelectedClassifier(classifier);
          updateClassifierBox(classifierBox, classifiers); // Refresh styles
        });

        classifierBox.getChildren().add(classifierLabel);
      }
    }
  }

  private void updateRightPane(BorderPane root, String classifier) {
    BorderPane rightPane = new BorderPane();
    rightPane.setStyle("-fx-background-color: #2c2c2c;");

    // Top section: Active model name

    Label activeModelLabel = new Label("Active model - " + classifier);
    activeModelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
    activeModelLabel.setTextFill(Color.PURPLE);
    BorderPane.setAlignment(activeModelLabel, Pos.CENTER);
    rightPane.setTop(activeModelLabel);

    // Bottom section: Sample button
    Button sampleButton = new Button("Sample");
    sampleButton.setFont(Font.font("Arial", FontWeight.BOLD, 35));
    sampleButton.setTextFill(Color.PURPLE);
    sampleButton.setStyle("-fx-background-color: transparent; -fx-border-color: #37c6ff;");
    sampleButton.setOnAction(event -> {
      controller.sampleCurrentModel();
    });

    BorderPane.setAlignment(sampleButton, Pos.CENTER);
    rightPane.setBottom(sampleButton);

    root.setCenter(rightPane);
  }

  public void updateClassifiers() {
    updateClassifierBox(classifierBox, controller.getClassifiers());
  }
}

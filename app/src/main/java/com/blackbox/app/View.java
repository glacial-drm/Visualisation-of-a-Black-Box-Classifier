package com.blackbox.app;

import com.blackbox.app.ui_elements.UI_Button;
import com.blackbox.app.ui_elements.UI_Mode;
import com.sun.javafx.scene.SpotLightHelper;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
  public static UI_Mode mode;

  @Override
  public void start(Stage primaryStage) {
    mode = UI_Mode.DARK;

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
    folderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
    folderLabel.setTextFill(Color.PURPLE);
    //folderLabel.setStyle(" -fx-padding: 20;");

    // Classifiers
    classifierBox = new VBox(15);
    classifierBox.setAlignment(Pos.TOP_CENTER);
    classifierBox.setStyle("-fx-padding: 20 0 20 0;");

    List<String> classifiers = controller.getClassifiers();
    updateClassifierBox(classifierBox, classifiers);

    // Set active model
    UI_Button setButton = new UI_Button("Set");
    setButton.setOnAction(event -> {
      // Update Right Part
      String selectedClassifier = controller.getSelectedClassifier();
      if (selectedClassifier != null) {
        updateRightPane(root, selectedClassifier);
      } else {
        updateRightPane(root, "No classifier selected");
      }
    });

    UI_Button loadButton = new UI_Button("Load");
    loadButton.setOnAction(event -> {
      DirectoryChooser chooser = controller.getFileDialog();
      File selectedDirectory = chooser.showDialog(primaryStage);
      controller.loadAllOnnx(selectedDirectory);
      updateClassifiers();
    });

    // Sample button
    UI_Button sampleButton = new UI_Button("Sample!");
    sampleButton.setOnAction(event -> {
      controller.sampleCurrentModel();
    });
    BorderPane.setAlignment(sampleButton, Pos.CENTER);

    VBox.setVgrow(classifierBox, Priority.ALWAYS);
    updateRightPane(root, "No classifier selected");
    leftPane.getChildren().addAll(folderLabel, classifierBox);

    // Bottom Pane
    BorderPane bottomPane = new BorderPane();

    bottomPane.setPrefWidth(200);
    bottomPane.setPrefHeight(125);
    bottomPane.setStyle("-fx-background-color: #1e1e1e;");

    // ComboBox (Dropdown) to select Model type
    ComboBox<String> dropdown = new ComboBox<>();
    dropdown.getItems().addAll("Support Vector Machine (SVM)","Decision Tree", "Neural Network (NN)");

    dropdown.setValue("Select Model");
    /*
      Dropdown default text styling seems awkward in inline css
     */
    dropdown.setStyle("-fx-background-color: #2c2c2c; -fx-font-family: Arial; -fx-text-fill: #ffffff;");
    //dropdown.getButtonCell().setStyle("-fx-background-color: #2c2c2c; -fx-font-family: Arial;");

    dropdown.setPrefWidth(200);
    dropdown.setPrefHeight(50);

    // VBox stores above dropdown and buttons
    VBox modelBox = new VBox();
    modelBox.getChildren().addAll(dropdown, loadButton, setButton, sampleButton);

    // HNox stores BorderPane bottom elements horizontally
    HBox detailsBox = new HBox();
    detailsBox.setStyle("-fx-background-color: #1e1e1e;");
    detailsBox.setPrefWidth(90);
    detailsBox.setPrefHeight(90);

    // VBox Stores Navigation information (xyz and zoom) and any other relevant output
    VBox navBox = new VBox();
    navBox.setStyle("-fx-background-color: #1e1e1e;");
    navBox.setPrefWidth(300);
    navBox.setPrefHeight(90);

    Label navigationLabel = new Label("Navigation");
    navigationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    navigationLabel.setTextFill(Color.PURPLE);
    navigationLabel.setPrefWidth(300);

    navBox.getChildren().addAll(navigationLabel);

    // VBox Stores ___
    VBox placeholderBox = new VBox();
    placeholderBox.setStyle("-fx-background-color: #1e1e1e;");
    placeholderBox.setPrefWidth(300);
    placeholderBox.setPrefHeight(90);

    Label placeholderLabel = new Label("Placeholder");
    placeholderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    placeholderLabel.setTextFill(Color.PURPLE);
    placeholderLabel.setPrefWidth(300);

    placeholderBox.getChildren().addAll(placeholderLabel);

    detailsBox.getChildren().addAll(navBox/*, placeholderBox*/);


    bottomPane.setLeft(modelBox);
    bottomPane.setCenter(detailsBox);
    bottomPane.setRight(placeholderBox);

    // Set layout
    root.setBottom(bottomPane);
    root.setLeft(leftPane);

    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Blackbox Classifier");
    primaryStage.show();
  }

  private void updateClassifierBox(VBox classifierBox, List<String> classifiers) {
    classifierBox.getChildren().clear();
    if (classifiers.isEmpty()) {
      Label emptyLabel = new Label("No classifiers available");
      emptyLabel.setWrapText(true);
      emptyLabel.setTextAlignment(TextAlignment.CENTER);
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
    activeModelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
    activeModelLabel.setTextFill(Color.PURPLE);
    BorderPane.setAlignment(activeModelLabel, Pos.CENTER);
    rightPane.setTop(activeModelLabel);

    // Left section: Feature Listing

    VBox featureBox = new VBox();
    featureBox.setStyle("-fx-background-color: #2c2c2c;");

    Label featureLabel = new Label("Features");
    featureLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    featureLabel.setTextFill(Color.PURPLE);
    featureLabel.setPrefWidth(300);
    BorderPane.setAlignment(featureLabel, Pos.CENTER);

    ScrollPane featurePane = new ScrollPane();

    featureBox.getChildren().addAll(featureLabel);
    rightPane.setLeft(featureBox);

    // Right Section : Creating a grid of points
    /*
    Connector file on BOX114 branch contains:
      PointData[] pointDatas = DataGen.genCircle(10000, 3f, 1.0, true, false, false, true);
      Use these params with some function to generate grid of points(?)
     */
    VBox gridBox = new VBox();
    gridBox.setStyle("-fx-background-color: #2c2c2c;");

    Label gridLabel = new Label("Create Grid Points");
    gridLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    gridLabel.setTextFill(Color.PURPLE);
    gridLabel.setPrefWidth(300);
    BorderPane.setAlignment(gridLabel, Pos.CENTER);

    gridBox.getChildren().addAll(gridLabel);
    rightPane.setCenter(gridBox);

    root.setCenter(rightPane);
  }

  public void updateClassifiers() {
    updateClassifierBox(classifierBox, controller.getClassifiers());
  }
}

package com.blackbox.app;

import com.blackbox.generators.DataGenerator;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class View extends Application {
  /**
   * The controller.
   */
  static Controller controller;
  private VBox classifierBox;
  private VBox requestBox;

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
      File folder = folderSelector(primaryStage);
      if (folder != null) {
        controller.loadAllOnnx(folder);
      }
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

    VBox vbox = new VBox();
    rightPane.setCenter(vbox);

    // Global generator settings

    Label globalGeneratorSettingsLabel = new Label("Global Generator Settings");
    globalGeneratorSettingsLabel.setStyle("-fx-font-size: 20px;");
    vbox.getChildren().add(globalGeneratorSettingsLabel);

    vbox.getChildren().add(controller.getGenOptions().createNode());

    // Date generator selector

    Label generatorSettingsLabel = new Label("Generator Settings");
    generatorSettingsLabel.setStyle("-fx-font-size: 20px;");
    vbox.getChildren().add(generatorSettingsLabel);

    ComboBox<DataGenerator> generatorSelector = new ComboBox<>();
    generatorSelector.setItems(FXCollections.observableList(Arrays.asList(controller.getGenerators())));
    generatorSelector.setConverter(new StringConverter<DataGenerator>() {
      @Override
      public DataGenerator fromString(String name) {
        return controller.getGeneratorByName(name);
      }

      @Override
      public String toString(DataGenerator generator) {
        return generator != null
          ? generator.getClass().getSimpleName()
          : "";
      }
    });
    vbox.getChildren().add(generatorSelector);

    // Data generator settings

    VBox generatorBox = new VBox();
    vbox.getChildren().add(generatorBox);

    generatorSelector.getSelectionModel().selectedItemProperty().addListener((_observer, _old, generator) -> {
      generatorBox.getChildren().clear();
      generatorBox.getChildren().add(generator.createNode());
    });
    generatorSelector.getSelectionModel().selectedIndexProperty().addListener((_observer, _old, index) -> {
      controller.setSelectedGenerator(index.intValue());
    });
    generatorSelector.getSelectionModel().select(0);

    // Training request selector

    Label trainingSettingsLabel = new Label("Training Settings");
    trainingSettingsLabel.setStyle("-fx-font-size: 20px;");
    vbox.getChildren().add(trainingSettingsLabel);

    ComboBox<Builder<?>> requestSelector = new ComboBox<>();
    requestSelector.setItems(FXCollections.observableList(Arrays.asList(controller.getTrainingRequests())));
    requestSelector.setConverter(new StringConverter<Builder<?>>() {
      @Override
      public Builder<?> fromString(String name) {
        return controller.getTrainingRequestByName(name);
      }

      @Override
      public String toString(Builder<?> request) {
        return request != null
          ? request.getDescriptorForType().getName()
          : "";
      }
    });
    vbox.getChildren().add(requestSelector);

    // Training request settings

    requestBox = new VBox();
    vbox.getChildren().add(requestBox);

    requestSelector.getSelectionModel().selectedItemProperty().addListener((observer, old, builder) -> {
      requestBox.getChildren().clear();
      requestBox.getChildren().add(new ProtoUi(builder).build());
    });
    requestSelector.getSelectionModel().selectedIndexProperty().addListener((_observer, _old, index) -> {
      controller.setSelectedTrainingRequest(index.intValue());
    });
    requestSelector.getSelectionModel().select(0);

    // Train button

    Button trainButton = new Button("Train");
    trainButton.setFont(Font.font("Arial", FontWeight.BOLD, 35));
    trainButton.setTextFill(Color.PURPLE);
    trainButton.setStyle("-fx-background-color: transparent; -fx-border-color: #37c6ff;");
    trainButton.setOnAction(event -> {
      controller.trainModel();
    });
    vbox.getChildren().add(trainButton);

    root.setCenter(rightPane);
  }

  public void updateClassifiers() {
    updateClassifierBox(classifierBox, controller.getClassifiers());
  }

  /**
   * Open a folder selector.
   *
   * This will use JavaFX on Windows and Mac, and tinyfd on Linux.
   *
   * Returns null if the action was cancelled.
   */
  public File folderSelector(Stage stage) {
    File selectedDirectory;

    if (System.getProperty("os.name").equals("Linux")) {
      String path = TinyFileDialogs.tinyfd_selectFolderDialog("", System.getProperty("user.dir"));
      if (path == null) return null;
      selectedDirectory = new File(path);
    } else {
      DirectoryChooser chooser = controller.getFileDialog();
      selectedDirectory = chooser.showDialog(stage);
    }

    return selectedDirectory;
  }
}

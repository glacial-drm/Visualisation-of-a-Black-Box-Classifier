package com.blackbox.app;

import com.blackbox.app.ui_elements.UI_Button;
import com.blackbox.app.ui_elements.UI_Mode;
import com.blackbox.generators.DataGenerator;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class View extends Application {
    /**
     * Time between allowing the controller to check for events.
     */
    private final Duration eventCheckInterval = Duration.millis(250);

    /**
     * The controller.
     */
    static Controller controller;
    private VBox classifierBox;
    private VBox requestBox;
    public static UI_Mode mode = UI_Mode.DARK;

    public void setupTimer() {
        // JavaFX's Timeline and KeyFrame are used instead of a normal
        // Java Timer so that this runs on the main JavaFX application thread.
        Timeline timer = new Timeline(
            new KeyFrame(eventCheckInterval, e -> controller.checkForEvents())
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * This runs the action in a new thread,
     * once completed it schedules the callback to run on the main UI thread,
     * so it can manipulate the UI safely.
     */
    private void runAsync(Runnable action, Runnable callback) {
        new Thread(() -> {
            action.run();
            Platform.runLater(callback);
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {
        setupTimer();

        primaryStage.setTitle("UON Blackbox Classifier");

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

        UI_Button loadButton = new UI_Button("Load");
        loadButton.setOnAction(event -> {
            File folder = folderSelector(primaryStage);
            if (folder != null) {
                controller.loadAllOnnx(folder);
            }
            updateClassifiers();
        });

        UI_Button sampleButton = new UI_Button("Sample");
        sampleButton.setOnAction(event -> {
            controller.sampleCurrentModel();
        });
        BorderPane.setAlignment(sampleButton, Pos.CENTER);

        VBox.setVgrow(classifierBox, Priority.ALWAYS);
        VBox centerBox = new VBox();
        root.setCenter(centerBox);
        settingsPanel(centerBox);
        leftPane.getChildren().addAll(folderLabel, classifierBox, loadButton);

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
        modelBox.getChildren().addAll(dropdown, loadButton, sampleButton);

        // HNox stores BorderPane bottom elements horizontally
        HBox detailsBox = new HBox();
        detailsBox.setStyle("-fx-background-color: #1e1e1e;");
        detailsBox.setPrefWidth(90);
        detailsBox.setPrefHeight(90);

        bottomPane.setLeft(modelBox);
        bottomPane.setCenter(detailsBox);

        // Set layout
        root.setBottom(bottomPane);
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

    private void globalDataGenSettingsPanel(Pane pane) {
        Label globalGeneratorSettingsLabel = new Label("Global Generator Settings");
        globalGeneratorSettingsLabel.setPrefWidth(400);
        globalGeneratorSettingsLabel.setStyle("-fx-font-size: 20px;");
        pane.getChildren().add(globalGeneratorSettingsLabel);

        pane.getChildren().add(controller.getGenOptions().createNode());
    }

    private void specificDataGenSettingsPanel(Pane pane) {
        // Selector

        Label generatorSettingsLabel = new Label("Generator Settings");
        generatorSettingsLabel.setStyle("-fx-font-size: 20px;");
        generatorSettingsLabel.setPrefWidth(400);
        pane.getChildren().add(generatorSettingsLabel);

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
        pane.getChildren().add(generatorSelector);

        // Settings

        VBox generatorBox = new VBox();
        pane.getChildren().add(generatorBox);

        generatorSelector.getSelectionModel().selectedItemProperty().addListener((_observer, _old, generator) -> {
            generatorBox.getChildren().clear();
            generatorBox.getChildren().add(generator.createNode());
        });
        generatorSelector.getSelectionModel().selectedIndexProperty().addListener((_observer, _old, index) -> {
            controller.setSelectedGenerator(index.intValue());
        });
        generatorSelector.getSelectionModel().select(0);
    }

    private void trainingSettingsPanel(Pane pane) {
        // Selector

        Label trainingSettingsLabel = new Label("Training Settings");
        trainingSettingsLabel.setStyle("-fx-font-size: 20px;");
        pane.getChildren().add(trainingSettingsLabel);

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
        pane.getChildren().add(requestSelector);

        // Settings

        requestBox = new VBox();
        pane.getChildren().add(requestBox);

        requestSelector.getSelectionModel().selectedItemProperty().addListener((observer, old, builder) -> {
            requestBox.getChildren().clear();
            requestBox.getChildren().add(new ProtoUi(builder).build());
        });
        requestSelector.getSelectionModel().selectedIndexProperty().addListener((_observer, _old, index) -> {
            controller.setSelectedTrainingRequest(index.intValue());
        });
        requestSelector.getSelectionModel().select(0);
    }

    private void settingsPanel(Pane pane) {
        Label gridLabel = new Label("Create Grid Points");
        gridLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gridLabel.setTextFill(Color.PURPLE);
        gridLabel.setPrefWidth(400);
        BorderPane.setAlignment(gridLabel, Pos.CENTER);

        pane.getChildren().addAll(gridLabel);

        globalDataGenSettingsPanel(pane);
        specificDataGenSettingsPanel(pane);
        trainingSettingsPanel(pane);

        // Train button

        UI_Button trainButton = new UI_Button("Train");
        trainButton.setOnAction(_event -> {
            trainButton.setText("Training...");
            runAsync(
                controller::trainModel,
                () -> trainButton.setText("Train")
            );
        });
        pane.getChildren().add(trainButton);
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

package com.blackbox.app;

import com.blackbox.generators.DataGenerator;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    private static enum Theme {
        LIGHT,
        DARK,
    }
    private Theme theme = Theme.LIGHT;

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
    private Stage primaryStage;
    private Scene scene;

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
        this.primaryStage = primaryStage;
        setupTimer();

        primaryStage.setTitle("UON Blackbox Classifier");

        // Main layout
        GridPane root = new GridPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(8));
        root.setHgap(8);
        root.setVgap(8);

        root.add(leftPanel(), 0, 0);
        root.add(modelInfoPanel(), 1, 0);
        root.add(dataGenSettingsPanel(), 2, 0);
        root.add(trainingSettingsPanel(), 3, 0);

        scene = new Scene(root, 800, 600);
        loadLightMode();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadLightMode() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/styles/root.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/light.css").toExternalForm());
    }

    private void loadDarkMode() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/styles/root.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dark.css").toExternalForm());
    }

    private Pane modelPanel() {
        VBox pane = new VBox();

        // Folder Name
        Label folderLabel = new Label("Folder Name");
        folderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        folderLabel.setTextFill(Color.PURPLE);
        folderLabel.setStyle(" -fx-padding: 20;");
        pane.getChildren().add(folderLabel);

        // Classifiers
        classifierBox = new VBox(15);
        VBox.setVgrow(classifierBox, Priority.ALWAYS);
        classifierBox.setAlignment(Pos.TOP_CENTER);
        classifierBox.setStyle("-fx-padding: 20 0 20 0;");
        pane.getChildren().add(classifierBox);

        List<String> classifiers = controller.getClassifiers();
        updateClassifierBox(classifierBox, classifiers);

        return pane;
    }

    private Pane optionsPanel() {
        VBox pane = new VBox();
        pane.getStyleClass().add("panel");

        Label title = new Label("Options");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        Button themeBtn = new Button("Light Mode");
        themeBtn.setOnAction(_e -> {
            if (theme == Theme.LIGHT) {
                theme = Theme.DARK;
                loadDarkMode();
                themeBtn.setText("Dark Mode");
            } else {
                theme = Theme.LIGHT;
                loadLightMode();
                themeBtn.setText("Light Mode");
            }
        });
        pane.getChildren().add(themeBtn);

        return pane;
    }

    private Pane trainingPanel() {
        VBox pane = new VBox();

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> {
            File folder = folderSelector(primaryStage);
            if (folder != null) {
                controller.loadAllOnnx(folder);
            }
            updateClassifiers();
        });
        pane.getChildren().add(loadButton);

        Button sampleButton = new Button("Sample");
        sampleButton.setOnAction(event -> {
            controller.sampleCurrentModel();
        });
        BorderPane.setAlignment(sampleButton, Pos.CENTER);
        pane.getChildren().add(sampleButton);

        Button trainButton = new Button("Train");
        trainButton.setOnAction(_event -> {
            trainButton.setText("Training...");
            runAsync(
                controller::trainModel,
                () -> trainButton.setText("Train")
            );
        });
        pane.getChildren().add(trainButton);

        return pane;
    }

    private Pane leftPanel() {
        VBox pane = new VBox();
        pane.getStyleClass().add("panel");
        pane.getChildren().addAll(
            modelPanel(),
            new Separator(),
            optionsPanel(),
            new Separator(),
            trainingPanel()
        );
        return pane;
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

    private Pane modelInfoPanel() {
        VBox pane = new VBox();
        pane.getStyleClass().add("panel");

        Label title = new Label("Model Info");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        return pane;
    }

    private Pane globalDataGenSettingsPanel() {
        VBox pane = new VBox();

        Label title = new Label("Global Generator Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        pane.getChildren().add(controller.getGenOptions().createNode());

        return pane;
    }

    private Pane specificDataGenSettingsPanel() {
        VBox pane = new VBox();

        // Selector

        Label title = new Label("Generator Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

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

        return pane;
    }

    public Pane dataGenSettingsPanel() {
        VBox pane = new VBox(
            globalDataGenSettingsPanel(),
            specificDataGenSettingsPanel()
        );
        pane.getStyleClass().add("panel");
        return pane;
    }

    private Pane trainingSettingsPanel() {
        VBox pane = new VBox();
        pane.getStyleClass().add("panel");

        // Selector

        Label title = new Label("Training Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

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

        return pane;
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

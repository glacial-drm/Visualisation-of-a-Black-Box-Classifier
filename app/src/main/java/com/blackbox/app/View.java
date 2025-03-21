package com.blackbox.app;

import com.blackbox.generators.DataGenerator;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class View extends Application {
    private static enum Theme {
        LIGHT,
        DARK,
    }
    private Theme theme = Theme.DARK;

    private static enum Layout {
        STACKED,
        COLUMNS,
    }
    private Layout layout = Layout.COLUMNS;
    private final int layoutBoundaryWidth = 750;

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
    private Node[] content;

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
    private <T> void runAsync(Supplier<T> action, Consumer<T> callback) {
        new Thread(() -> {
            T value = action.get();
            Platform.runLater(() -> callback.accept(value));
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupTimer();

        primaryStage.setTitle("UON Blackbox Classifier");

        // Main layout

        content = new Node[] {
            leftPanel(),
            dataGenSettingsPanel(),
            trainingSettingsPanel(),
        };

        scene = new Scene(new VBox(), 800, 600);
        if (theme == Theme.LIGHT) {
            loadLightMode();
        } else {
            loadDarkMode();
        }

        scene.widthProperty().addListener((_observable, _old, value) -> {
            int width = value.intValue();

            if (width > layoutBoundaryWidth && layout == Layout.STACKED) {
                loadColumnsLayout();
            } else if (width < layoutBoundaryWidth && layout == Layout.COLUMNS) {
                loadStackedLayout();
            }
        });
        loadColumnsLayout();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadStackedLayout() {
        layout = Layout.STACKED;

        VBox box = new VBox(8);
        box.setPadding(new Insets(8));
        ScrollPane root = new ScrollPane(box);
        root.getStyleClass().add("root");
        root.setFitToWidth(true);

        for (Node node : content) {
            node.getStyleClass().add("panel");
            box.getChildren().add(node);
            VBox.setVgrow(node, Priority.ALWAYS);
        }

        scene.setRoot(root);
    }

    private void loadColumnsLayout() {
        layout = Layout.COLUMNS;

        GridPane root = new GridPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(8));
        root.setHgap(8);
        root.setVgap(8);

        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setPercentWidth(100 / 3);
        root.getColumnConstraints().addAll(
            colConstraints,
            colConstraints,
            colConstraints
        );

        int i = 0;
        for (Node node : content) {
            ScrollPane scrollPane = new ScrollPane(node);
            scrollPane.getStyleClass().add("panel");
            ((VBox)node).prefWidthProperty().bind(
                scrollPane.widthProperty().map(width ->
                    width.doubleValue()
                    - scrollPane.getPadding().getLeft()
                    - scrollPane.getPadding().getRight()
                )
            );
            root.add(scrollPane, i++, 0);
            GridPane.setVgrow(scrollPane, Priority.ALWAYS);
        }

        scene.setRoot(root);
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
        pane.setSpacing(8);
        Label folderLabel = new Label();

        Button loadButton = new Button("Load Folder");
        loadButton.setOnAction(_e -> {
            folderSelector(primaryStage);

            if (controller.getCurrentDir() != null) {
                String text = controller.getCurrentDir().toString();
                if (text.length() > 15) {
                    text = "..." + text.substring(text.length() - 12);
                }
                folderLabel.setText(text);
            }
            updateClassifiers();
        });
        pane.getChildren().add(loadButton);

        // Folder Name
        folderLabel.getStyleClass().add("title");
        pane.getChildren().add(folderLabel);

        // Classifiers
        classifierBox = new VBox(4);
        pane.getChildren().add(classifierBox);

        List<String> classifiers = controller.getClassifiers();
        updateClassifierBox(classifierBox, classifiers);

        // Sample Button
        Button sampleButton = new Button("Sample");
        sampleButton.setOnAction(_e -> {
            controller.sampleCurrentModel();
        });
        sampleButton.prefWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(sampleButton);

        return pane;
    }

    private Pane optionsPanel() {
        VBox pane = new VBox();
        pane.setSpacing(8);

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
        themeBtn.prefWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(themeBtn);

        return pane;
    }

    private Pane trainingPanel() {
        VBox pane = new VBox();
        pane.setSpacing(8);

        Button trainButton = new Button("Train + Sample");
        trainButton.setOnAction(_event -> {
            trainButton.setText("Training...");
            runAsync(
                controller::trainModel,
                path -> {
                    trainButton.setText("Train + Sample");
                    controller.loadAllOnnx();
                    controller.setSelectedClassifier(path);
                    updateClassifiers();
                    controller.sampleCurrentModel();
                }
            );
        });
        trainButton.prefWidthProperty().bind(pane.widthProperty());
        pane.getChildren().add(trainButton);

        return pane;
    }

    private Node leftPanel() {
        VBox pane = new VBox();
        pane.getChildren().addAll(
            modelPanel(),
            new Separator(),
            modelInfoPanel(),
            new Separator(),
            optionsPanel(),
            new Separator(),
            trainingPanel()
        );
        pane.setSpacing(16);
        return pane;
    }

    private void updateClassifierBox(VBox classifierBox, List<String> classifiers) {
        classifierBox.getChildren().clear();
        if (classifiers.isEmpty()) {
            Label emptyLabel = new Label("No classifiers available");
            classifierBox.getChildren().add(emptyLabel);
        } else {
            for (String classifier : classifiers) {
                HBox box = new HBox();
                box.setPadding(new Insets(4));
                Label label = new Label(classifier);

                String selectedClassifier = controller.getSelectedClassifier();
                if (classifier.equals(selectedClassifier)) {
                    box.getStyleClass().add("selected");
                }

                box.setOnMouseClicked(event -> {
                    controller.setSelectedClassifier(classifier);
                    // Refresh the styles
                    updateClassifierBox(classifierBox, classifiers);
                });

                box.getChildren().add(label);
                HBox.setHgrow(label, Priority.ALWAYS);
                classifierBox.getChildren().add(box);
            }
        }
    }

    private Pane modelInfoPanel() {
        VBox pane = new VBox();
        pane.setSpacing(8);

        Label title = new Label("Model Info");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        return pane;
    }

    private Pane globalDataGenSettingsPanel() {
        VBox pane = new VBox();
        pane.setSpacing(8);

        Label title = new Label("Global Generator Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        pane.getChildren().add(controller.getGenOptions().createNode());

        return pane;
    }

    private Pane specificDataGenSettingsPanel() {
        VBox pane = new VBox();
        pane.setSpacing(8);

        // Selector

        Label title = new Label("Generator Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        ComboBox<DataGenerator> generatorSelector = new ComboBox<>();
        generatorSelector.prefWidthProperty().bind(pane.widthProperty());
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

    public Node dataGenSettingsPanel() {
        VBox pane = new VBox(
            globalDataGenSettingsPanel(),
            specificDataGenSettingsPanel()
        );
        pane.setSpacing(16);
        return pane;
    }

    private Node trainingSettingsPanel() {
        VBox pane = new VBox();

        // Selector

        Label title = new Label("Training Settings");
        title.getStyleClass().add("title");
        pane.getChildren().add(title);

        ComboBox<Builder<?>> requestSelector = new ComboBox<>();
        requestSelector.prefWidthProperty().bind(pane.widthProperty());
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
     * Does nothing if the action was cancelled.
     */
    public void folderSelector(Stage stage) {
        File selectedDirectory;

        if (System.getProperty("os.name").equals("Linux")) {
            String path = TinyFileDialogs.tinyfd_selectFolderDialog("", System.getProperty("user.dir"));
            if (path == null) return;
            selectedDirectory = new File(path);
        } else {
            DirectoryChooser chooser = controller.getFileDialog();
            selectedDirectory = chooser.showDialog(stage);
        }

        controller.openNewFolder(selectedDirectory);
    }
}

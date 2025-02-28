package com.blackbox.app;

import com.blackbox.app.events.ControllerEvent;
import com.blackbox.eventqueue.EventConsumer;
import com.blackbox.eventqueue.EventProducer;
import com.blackbox.generators.CircleGenerator;
import com.blackbox.generators.CubeGenerator;
import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.ClearPoints;
import com.blackbox.graph.events.PointsLoaded;
import com.blackbox.graph.events.StopEvent;
import com.blackbox.model.OnnxModel;
import com.blackbox.model.PointData;
import com.blackbox.model.Sampler;
import com.blackbox.proto.connector.SvcRequest;
import com.blackbox.proto.connector.UiTest;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.tinylog.Logger;

public class Controller {
  private final EventConsumer<ControllerEvent> eventQueue;
  /**
   * A message queue going to the {@link com.blackbox.graph.Canvas Canvas}.
   * <p>
   * Messages passed here will be processed in the canvas render loop.
   */
  private final EventProducer<CanvasEvent> canvasEventSink;
  DirectoryChooser chooser = new DirectoryChooser();
  /**
   * The model.
   */
  private final Model model;

  Connector connector = new Connector();

  /**
   * Constructor for the Controller.
   *
   * @param model the model instance to work with
   */
  public Controller(
    Model model,
    EventConsumer<ControllerEvent> eventQueue,
    EventProducer<CanvasEvent> canvasEventSink
  ) {
    this.model = model;
    this.eventQueue = eventQueue;
    this.canvasEventSink = canvasEventSink;

    model.setGenerators(new DataGenerator[] {
      new CircleGenerator(), new CubeGenerator(),
    });
    for (DataGenerator generator : getGenerators()) {
      generator.setOnChange(_v -> {
      clearPoints();
      generateTrainingData();
      showTrainingData();
      });
    }
    getGenOptions().setOnChange(_v -> {
      clearPoints();
      generateTrainingData();
      showTrainingData();
    });

    model.setTrainingRequests(new Builder[] {
      // This specifies the default values for all the training requests
      UiTest.newBuilder()
        .setBoolean(true)
        .setF32(1f)
        .setF64(2.0)
        .setI32(3)
        .setI64(4)
        .setStr("Hello world")
        .setTestEnum(UiTest.TestEnum.OPTION2),
      SvcRequest.newBuilder()
    });

    generateTrainingData();
    showTrainingData();

    // Register a listener to update the view when the model changes
    model.addChangeListener(voidParam -> onModelChanged());
    model.initClassifiers();
    initFileLoader();
  }

  /**
   * Check for received events and respond to them.
   */
  public void checkForEvents() {
    while (!eventQueue.isEmpty()) {
      eventQueue.poll();
    }
  }

  /**
   * Send the {@link ClearPoints} event.
   */
  public void clearPoints() {
    canvasEventSink.send(new ClearPoints());
  }

  /**
   * Generate new training data with the selected generator.
   * This doesn't show the training data on the Canvas,
   * use {@link #showTrainingData()} for that.
   */
  public void generateTrainingData() {
    model.setTrainingData(getSelectedGenerator().buildData(getGenOptions()));
  }

  /**
   * Show the previously generated training data on the Canvas.
   * Use in conjunction with {@link #clearPoints()}.
   */
  public void showTrainingData() {
    for (PointData point : model.getTrainingData()) {
      pointLoaded(point, true);
    }
  }

  public GlobalGenOptions getGenOptions() {
    return model.getGetOptions();
  }

  public DataGenerator[] getGenerators() {
    return model.getGenerators();
  }

  public DataGenerator getSelectedGenerator() {
    return model.getSelectedGenerator();
  }

  /**
   * Get the dimension of the selected generator.
   */
  public int getDimension() {
    return getSelectedGenerator().getDimension();
  }

  public DataGenerator getGeneratorByName(String name) {
    DataGenerator target = null;
    for (DataGenerator generator : model.getGenerators()) {
      if (generator.getClass().getSimpleName().equals(name)) {
        target = generator;
      }
    }
    return target;
  }

  public void setSelectedGenerator(int index) {
    model.setSelectedGenerator(index);
  }

  public Builder<?>[] getTrainingRequests() {
    return model.getTrainingRequests();
  }

  public Builder<?> getTrainingRequestByName(String name) {
    Builder<?> target = null;
    for (Builder<?> builder : model.getTrainingRequests()) {
      if (builder.getDescriptorForType().getName().equals(name)) {
        target = builder;
      }
    }
    return target;
  }

  public void setSelectedTrainingRequest(int index) {
    model.setSelectedTrainingRequest(index);
  }

  /**
   * Trains a model with the currently selected settings and training data.
   */
  public void trainModel() {
    connector.trainModel(
      model.getSelectedTrainingRequest(),
      model.getTrainingData()
    );
  }

  private void initFileLoader() {
    chooser = new DirectoryChooser();
    chooser.setTitle("Select Classifier");
  }

  /**
   * Send the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
   *
   * @param point The point data.
   */
  public void pointLoaded(PointData point, boolean isTraining) {
    canvasEventSink.send(new PointsLoaded(point, isTraining));
  }

  /**
   * Send the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
   */
  public void stop() {
    // Called with true to also kill this thread
    // May be changed in the future when windows can be open independently.
    canvasEventSink.send(new StopEvent(true));
  }

  /**
   * Get the message.
   */
  public String getMessage() {
    return model.message;
  }

  // Other getters
  public String[] getMenuList() {
    return model.menuList;
  }

  public String[] getFileList() {
    return model.fileList;
  }

  /**
   * gets the list of classifier names.
   *
   * @return a list of strings representing classifier names
   */
  public List<String> getClassifiers() {
    return model.getClassifiers();
  }

  /**
   * Sets a new list of classifiers and notifies listeners.
   *
   * @param classifiers a new List of classifier names as Strings
   */
  public void setClassifiers(List<String> classifiers) {
    model.setClassifiers(classifiers);
  }

  /**
   * Adds a classifier to the existing list and notifies listeners.
   *
   * @param classifier the classifier name to add
   */
  public void addClassifier(String classifier) {
    model.addClassifier(classifier);
  }

  public void removeClassifier(String classifier) {
    model.removeClassifier(classifier);
  }

  public void clearClassifiers() {
    model.clearClassifiers();
  }

  /**
   * gets navigation details for display in the navigation section.
   *
   * @return a string with navigation instructions or information
   */
  public String getNavigationDetails() {
    return model.getNavigationDetails();
  }

  /**
   * Sets new navigation details and notifies listeners.
   *
   * @param navigationDetails the new navigation details as a String
   */
  public void setNavigationDetails(String navigationDetails) {
    model.setNavigationDetails(navigationDetails);
  }

  /**
   * Handle the selection of a classifier from the ComboBox.
   *
   * @param selectedClassifier the selected classifier name
   */
  public void onClassifierSelected(String selectedClassifier) {
    Logger.info("Selected classifier: " + selectedClassifier);
  }

  /**
   * Responds to model changes.
   */
  private void onModelChanged() {
    Logger.info("Model has changed. Notify the view to update components.");
  }

  /**
   * Run the sampler, sending the output to the event queue.
   * This makes input data spread across a hard-coded range.
   */
  public void runSampler(String path) {
    OnnxModel onnx;
    try {
      onnx = new OnnxModel(path);
    } catch (Exception err) {
      System.err.println("ONNX file not found");
      return;
    }

    Sampler sampler = new Sampler(onnx);

    // Prepare input data

    //Check number of features of the model
    int num_features = onnx.getNumInputFeatures();
    Logger.info("number of features: " + num_features);

    // Total numbers of points will be resolution * resolution
    int resolution = 40;
    // Input data will start be from -range .. +range
    float range = 3.0f;

    float pointGap = range * 2f / (float) (resolution - 1);
    // Needed for the outer edge
    float epsilon = 0.00001f;

    int totalPoints = (int) Math.pow(resolution, num_features);

    float[][] input = new float[totalPoints][num_features];

    int index = 0;

    if(num_features == 3 ) {
      for (float x = -range; x <= range + epsilon; x += pointGap) {
        for (float y = -range; y <= range + epsilon; y += pointGap) {
          for (float z = -range; z <= range + epsilon; z += pointGap) {
            input[index][0] = x;
            input[index][1] = y;
            input[index][2] = z;
            index++;
          }
        }
      }
    }
    else {
      for (float x = -range; x <= range + epsilon; x += pointGap) {
        for (float y = -range; y <= range + epsilon; y += pointGap) {
          input[index][0] = x;
          input[index][1] = y;
          index++;

        }
      }
    }

    // Get output

    List<PointData> points = sampler.sampleBC(input);

    for (PointData point : points) {
      pointLoaded(point, false);
    }

    onnx.endSession();
  }

  public String getSelectedClassifier() {
    return model.getSelectedClassifier();
  }

  public void setSelectedClassifier(String classifier) {
    model.setSelectedClassifier(classifier);
  }

  public DirectoryChooser getFileDialog() {
    return chooser;
  }

  private String getExt(File file) {
    String extension = "";
    String fileName = file.getName();

    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      extension = fileName.substring(i + 1);
    }
    return extension;
  }

  public void loadAllOnnx(File selectedDirectory) {
    if (selectedDirectory == null) {
      return;
    }

    model.setCurrentDir(selectedDirectory);
    model.clearClassifiers();

    for (File file : Objects.requireNonNull(selectedDirectory.listFiles())) {
      if (!file.isDirectory()) {
        String ext = getExt(file);
        if (ext.equals("onnx")) {
          model.addClassifier(file.getName());
          Logger.info("Loading onnx file: " + file.getName());
        }
      }
    }
  }

  public void sampleCurrentModel() {
    // Clear current points and reshow training data
    clearPoints();
    showTrainingData();
    Logger.info("Sampling");
    String path = model.getCurrentDir().getAbsolutePath() + "/" + model.getSelectedClassifier();
    Logger.info("Selected classifier: " + path);
    runSampler(path);
  }
}

package com.blackbox.app;

import com.blackbox.app.events.CloseGUI;
import com.blackbox.app.events.ControllerEvent;
import com.blackbox.eventqueue.EventConsumer;
import com.blackbox.eventqueue.EventProducer;
import com.blackbox.generators.*;
import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.ClearPoints;
import com.blackbox.graph.events.DimensionChange;
import com.blackbox.graph.events.PointsLoaded;
import com.blackbox.graph.events.StopEvent;
import com.blackbox.model.OnnxModel;
import com.blackbox.model.PointData;
import com.blackbox.model.Sampler;
import com.blackbox.proto.connector.*;
import com.google.protobuf.GeneratedMessage.Builder;

import javafx.application.Platform;

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
  /**
   * The model.
   */
  private final Model model;

  private final Connector connector;

  /**
   * Constructor for the Controller.
   *
   * @param model the model instance to work with
   */
  public Controller(
    Model model,
    EventConsumer<ControllerEvent> eventQueue,
    EventProducer<CanvasEvent> canvasEventSink,
    Connector connector
  ) {
    this.model = model;
    this.eventQueue = eventQueue;
    this.canvasEventSink = canvasEventSink;
    this.connector = connector;

    model.setGenerators(new DataGenerator[] {
      new CircleGenerator(),
      new CubeGenerator(),
      new ExprGenerator2D(),
      new ExprGenerator3D(),
    });
    for (DataGenerator generator : getGenerators()) {
      generator.setOnChange(_v -> genAndShowTrainingData());
    }
    getGenOptions().setOnChange(_v -> genAndShowTrainingData());

    model.setTrainingRequests(new Builder[]{
    // This specifies the default values for all the training requests
    UiTest.newBuilder().setBoolean(true).setF32(1f).setF64(2.0).setI32(3).setI64(4).setStr("Hello world").setTestEnum(UiTest.TestEnum.OPTION2),
            SvmRequest.newBuilder(), DecisionTreeRequest.newBuilder().setCriterion(Criterion.GINI).setMaxDepth(-1),
            RandomForestRequest.newBuilder().setNEstimators(100).setCriterion(Criterion.GINI).setMaxDepth(-1).setMinSamplesSplit(2).setMinSamplesLeaf(1).setMaxFeatures(RandomForestRequest.MaxFeatures.SQRT),
            KNeighborsRequest.newBuilder().setNNeighbors(5).setWeights(KNeighborsRequest.Weights.UNIFORM).setAlgorithm(KNeighborsRequest.Algorithm.AUTO).setLeafSize(30).setP(2).setMetric(KNeighborsRequest.Metric.MINKOWSKI).setNJobs(1),
            LogisticRegressionRequest.newBuilder().setPenalty(LogisticRegressionRequest.Penalty.L2).setDual(false).setTol(1e-4F).setC(1).setFitIntercept(true).setInterceptScaling(1).setSolver(LogisticRegressionRequest.Solver.LBFGS).setMaxIter(100).setWarmStart(false).setNJobs(1)
    });

    generateTrainingData();
    showTrainingData();

    model.initClassifiers();
  }

  ///////////////////////////////////////////////////////////////////////////
  ///  File Loading                                                       ///
  ///////////////////////////////////////////////////////////////////////////

  public void openNewFolder(File folder) {
    model.setCurrentDir(folder);
    loadAllOnnx();
  }

  public File getCurrentDir() {
    return model.getCurrentDir();
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

  public void loadAllOnnx() {
    if (model.getCurrentDir() == null) {
      return;
    }

    model.clearClassifiers();

    for (File file : Objects.requireNonNull(model.getCurrentDir().listFiles())) {
      if (!file.isDirectory()) {
        String ext = getExt(file);
        if (ext.equals("onnx")) {
          model.addClassifier(file.getName());
          Logger.info("Loading onnx file: {}", file.getName());
        }
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  ///  Control Flow                                                       ///
  ///////////////////////////////////////////////////////////////////////////

  /**
   * Clears the currently shown training data,
   * generates new training data,
   * and shows that on the canvas.
   */
  public void genAndShowTrainingData() {
    clearPoints();
    generateTrainingData();
    showTrainingData();
  }

  /**
   * Check for received events and respond to them.
   */
  public void checkForEvents() {
    while (!eventQueue.isEmpty()) {
      ControllerEvent event = eventQueue.poll();

      if(event == null) {
        continue;
      }
      if(event instanceof CloseGUI){
        stop();
      }
    }
  }

  public void closeConnector() {
    connector.close();
  }

  /**
   * Send the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
   */
  public void stop() {
    // Called with true to also kill this thread
    // May be changed in the future when windows can be open independently.
    canvasEventSink.send(new StopEvent(true));
    Platform.exit();
  }

  ///////////////////////////////////////////////////////////////////////////
  ///  Canvas                                                             ///
  ///////////////////////////////////////////////////////////////////////////

  /**
   * Send the {@link ClearPoints} event.
   */
  public void clearPoints() {
    canvasEventSink.send(new ClearPoints());
  }

  /**
   * Send the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
   *
   * @param point The point data.
   */
  public void pointLoaded(PointData point, boolean isTraining) {
    canvasEventSink.send(new PointsLoaded(point, isTraining));
  }

  ///////////////////////////////////////////////////////////////////////////
  ///  Training Data Generation                                           ///
  ///////////////////////////////////////////////////////////////////////////

  public GlobalGenOptions getGenOptions() {
    return model.getGenOptions();
  }

  public DataGenerator[] getGenerators() {
    return model.getGenerators();
  }

  public DataGenerator getSelectedGenerator() {
    return model.getSelectedGenerator();
  }

  public void setSelectedGenerator(int index) {
    model.setSelectedGenerator(index);
    DataGenerator selectedGenerator = model.getSelectedGenerator();
    canvasEventSink.send(new DimensionChange(selectedGenerator.getDimension()));
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

  ///////////////////////////////////////////////////////////////////////////
  ///  Model Training                                                     ///
  ///////////////////////////////////////////////////////////////////////////

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
   * Returns the created ONNX path.
   */
  public String trainModel() {
    return connector.trainModel(
      model.getSelectedTrainingRequest(),
      model.getTrainingData()
    );
  }

  ///////////////////////////////////////////////////////////////////////////
  ///  Model Sampling                                                     ///
  ///////////////////////////////////////////////////////////////////////////

  /**
   * gets the list of classifier names.
   *
   * @return a list of strings representing classifier names
   */
  public List<String> getClassifiers() {
    return model.getClassifiers();
  }

  public String getSelectedClassifier() {
    return model.getSelectedClassifier();
  }

  public void setSelectedClassifier(String classifier) {
    model.setSelectedClassifier(classifier);
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
    Logger.info("number of features: {}", num_features);

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

  public void sampleCurrentModel() {
    // Clear current points and reshow training data
    clearPoints();
    showTrainingData();
    Logger.info("Sampling");
    String path = model.getCurrentDir().getAbsolutePath() + "/" + model.getSelectedClassifier();
    Logger.info("Selected classifier: {}", path);
    runSampler(path);
  }
}

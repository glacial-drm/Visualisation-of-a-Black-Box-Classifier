package com.blackbox.app;

import com.blackbox.app.events.*;
import com.blackbox.eventqueue.EventConsumer;
import com.blackbox.eventqueue.EventProducer;
import com.blackbox.generators.*;
import com.blackbox.graph.Input.InputHandler;
import com.blackbox.graph.events.*;
import com.blackbox.model.OnnxModel;
import com.blackbox.model.PointData;
import com.blackbox.model.Sampler;
import com.blackbox.proto.connector.*;
import com.google.protobuf.GeneratedMessage.Builder;
import javafx.application.Platform;
import org.tinylog.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public Controller(Model model, EventConsumer<ControllerEvent> eventQueue, EventProducer<CanvasEvent> canvasEventSink, Connector connector) {
        this.model = model;
        this.eventQueue = eventQueue;
        this.canvasEventSink = canvasEventSink;
        this.connector = connector;

        model.setGenerators(new DataGenerator[]{new CircleGenerator(), new CubeGenerator(), new ExprGenerator2D(), new ExprGenerator3D(),});
        for (DataGenerator generator : getGenerators()) {
            generator.setOnChange(_v -> genAndShowTrainingData());
        }
        getGenOptions().setOnChange(_v -> genAndShowTrainingData());

        model.setTrainingRequests(new Builder[]{
                // This specifies the default values for all the training requests
                UiTest.newBuilder().setBoolean(true).setF32(1f).setF64(2.0).setI32(3).setI64(4).setStr("Hello world").setTestEnum(UiTest.TestEnum.OPTION2), SvmRequest.newBuilder().setC(1f).setKernel(SvmRequest.Kernel.RBF).setPolyDegree(3).setGamma(SvmRequest.Gamma.SCALE).setCoef0(0f).setShrinking(true).setProbability(false).setTol(0.001f).setCacheSize(200).setMaxIter(-1).setBreakTies(false).setRandomState(42), DecisionTreeRequest.newBuilder().setCriterion(Criterion.GINI).setMaxDepth(-1), RandomForestRequest.newBuilder().setNEstimators(100).setCriterion(Criterion.GINI).setMaxDepth(-1).setMinSamplesSplit(2).setMinSamplesLeaf(1).setMaxFeatures(RandomForestRequest.MaxFeatures.SQRT), KNeighborsRequest.newBuilder().setNNeighbors(5).setWeights(KNeighborsRequest.Weights.UNIFORM).setAlgorithm(KNeighborsRequest.Algorithm.AUTO).setLeafSize(30).setP(2).setMetric(KNeighborsRequest.Metric.MINKOWSKI).setNJobs(1), LogisticRegressionRequest.newBuilder().setPenalty(LogisticRegressionRequest.Penalty.L2).setDual(false).setTol(1e-4F).setC(1).setFitIntercept(true).setInterceptScaling(1).setSolver(LogisticRegressionRequest.Solver.LBFGS).setMaxIter(100).setWarmStart(false).setNJobs(1), MLPClassifierRequest.newBuilder().setActivation(MLPClassifierRequest.Activation.relu).setSolver(MLPClassifierRequest.Solver.adam).setAlpha(0.0001f).setHiddenLayers("10,5")});

        canvasEventSink.send(new SamplingResolutionChanged(model.getSamplingResolution()));

        generateTrainingData();
        showTrainingData();

        model.initClassifiers();
    }

    /// ////////////////////////////////////////////////////////////////////////
    ///  File Loading                                                       ///
    /// ////////////////////////////////////////////////////////////////////////


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
     *
     * @return Unprocessed events for the View
     */
    public List<ControllerEvent> checkForEvents() {
        List<ControllerEvent> viewEvents = null;

        while (!eventQueue.isEmpty()) {
            ControllerEvent event = eventQueue.poll();

            if (event == null) {
                continue;
            }

            // Handle event in Controller
            if (event instanceof CloseGUI) {
                stop();
            } else if (event instanceof CanvasResize canvasResize) {
                model.setCanvasSize(canvasResize.width, canvasResize.height);
                canvasChange(model.getActiveModel());
            } else if (event instanceof CenterMoved centerMoved) {
                model.setCanvasCenter(centerMoved.position);
                canvasChange(model.getActiveModel());
            } else if (event instanceof FovChange fovChange) {
                model.setCanvasFov(fovChange.fov);
                canvasChange(model.getActiveModel());
            } else {
                // Handle event in View

                if (viewEvents == null) viewEvents = new ArrayList<>();
                viewEvents.add(event);
            }

            if (InputHandler.getZoom() != model.getPreviousZoom()) {
                model.setPreviousZoom(InputHandler.getZoom());
                canvasChange(model.getActiveModel());
            }
        }

        return viewEvents;
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

    /// ////////////////////////////////////////////////////////////////////////
    ///  Training Data Generation                                           ///
    /// ////////////////////////////////////////////////////////////////////////

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

    /// ////////////////////////////////////////////////////////////////////////
    ///  Model Training                                                     ///
    /// ////////////////////////////////////////////////////////////////////////

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
        return connector.trainModel(model.getSelectedTrainingRequest(), model.getTrainingData());
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
        model.setOnnxModel(null);
        model.setSelectedClassifier(classifier);
    }

    // TODO: Not a good function name give better name
    public void canvasChange(String path) {
        clearPoints();
        showTrainingData();

        runSampler(path);
    }

    public void runSampler(String path) {
        if (path == null) {
            return;
        }

        OnnxModel onnx = getOrCreateOnnxModel(path);
        if (onnx == null) {
            return;
        }
        Sampler sampler = new Sampler(onnx);

        // Log number of model input features.
        int numFeatures = onnx.getNumInputFeatures();
        Logger.info("Number of features: {}", numFeatures);

        // Prepare the input data.
        float[][] input;
        if (numFeatures == 2) {
            input = create2DInputGrid();
        } else {
            input = create3DInputGrid(numFeatures);
        }

        // Sample points and process them.
        List<PointData> points = sampler.sampleBC(input);
        for (PointData point : points) {
            pointLoaded(point, false);
        }
    }

    private OnnxModel getOrCreateOnnxModel(String path) {
        OnnxModel currentModel = model.getOnnxModel();
        if (currentModel == null || !path.equals(currentModel.getPath())) {
            if (currentModel != null) {
                currentModel.endSession();
            }
            try {
                OnnxModel newModel = new OnnxModel(path);
                model.setOnnxModel(newModel);

                Optional<PointData[]> trainingData = newModel.getTrainingData();
                if (trainingData.isPresent()) {
                    model.setTrainingData(trainingData.get());
                }

                return newModel;
            } catch (Exception e) {
                Logger.error("Failed to create OnnxModel: {}", e);
                return null;
            }
        }
        return currentModel;
    }

    private float[][] create3DInputGrid(int numFeatures) {
        // Calculate range based on the inverse zoom factor.
        float range = (float) InputHandler.getZoom();
        int resolution = 20;
        float pointGap = range * 2f / (resolution - 1);
        float epsilon = 0.00001f;

        int totalPoints = (int) Math.pow(resolution, numFeatures);
        float[][] input = new float[totalPoints][numFeatures];
        int index = 0;

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
        return input;
    }

    private float[][] create2DInputGrid() {
        float visibleHeight = (float) (2 * InputHandler.getZoom() * (float) Math.tan(model.getCanvasFov() / 2.0f));
        float visibleWidth = visibleHeight * model.getCanvasAspect();

        float left = model.getCanvasX() - visibleWidth / 2.0f;
        float right = model.getCanvasX() + visibleWidth / 2.0f;
        float bottom = model.getCanvasY() - visibleHeight / 2.0f;
        float top = model.getCanvasY() + visibleHeight / 2.0f;

        int yResolution = model.getSamplingResolution();
        int xResolution = (int) (yResolution * model.getCanvasAspect());
        float[][] input = new float[xResolution * yResolution][2];
        int index = 0;

        for (int i = 0; i < xResolution; i++) {
            float x = left + (right - left) * (i / (float) (xResolution - 1));
            for (int j = 0; j < yResolution; j++) {
                float y = bottom + (top - bottom) * (j / (float) (yResolution - 1));
                input[index][0] = x;
                input[index][1] = y;
                index++;
            }
        }
        return input;
    }

    public void sampleCurrentModel() {
        // Clear current points and reshow training data
        Logger.info("Sampling");
        canvasChange(model.getActiveModel());
    }

    public int getSamplingResolution() {
        return model.getSamplingResolution();
    }

    public void setSamplingResolution(int resolution) {
        model.setSamplingResolution(resolution);

        // Send the new resolution to the canvas
        canvasEventSink.send(new SamplingResolutionChanged(resolution));
    }
}

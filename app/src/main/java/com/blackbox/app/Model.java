package com.blackbox.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.model.OnnxModel;
import com.blackbox.model.PointData;
import com.blackbox.model.Sampler;
import com.google.protobuf.GeneratedMessage.Builder;
import org.joml.Vector3f;

public class Model {
    ///////////////////////////////////////////////////////////////////////////
    ///  Current Directory                                                  ///
    ///////////////////////////////////////////////////////////////////////////

    private File currentDir;

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File newDir) {
        currentDir = newDir;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///  Generator Options                                                  ///
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The global options for all the training data generators.
     */
    private GlobalGenOptions genOptions = new GlobalGenOptions();
    private int selectedGenerator;
    private DataGenerator[] generators;

    public GlobalGenOptions getGenOptions() {
        return genOptions;
    }

    public DataGenerator[] getGenerators() {
        return generators;
    }

    public DataGenerator getSelectedGenerator() {
        return generators[selectedGenerator];
    }

    public void setGenerators(DataGenerator[] generators) {
        this.generators = generators;
    }

    public void setSelectedGenerator(int index) {
        selectedGenerator = index;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///  Training Requests                                                  ///
    ///////////////////////////////////////////////////////////////////////////

    private int selectedTrainingRequest = 0;
    private Builder<?>[] trainingRequests;

    private PointData[] trainingData;

    public void setTrainingData(PointData[] data) {
        trainingData = data;
    }

    public PointData[] getTrainingData() {
        return trainingData;
    }

    public void setTrainingRequests(Builder<?>[] requests) {
        trainingRequests = requests;
    }

    public Builder<?>[] getTrainingRequests() {
        return trainingRequests;
    }

    public void setSelectedTrainingRequest(int index) {
        selectedTrainingRequest = index;
    }

    public Builder<?> getSelectedTrainingRequest() {
        return trainingRequests[selectedTrainingRequest];
    }

    ///////////////////////////////////////////////////////////////////////////
    ///  Classifiers                                                        ///
    ///////////////////////////////////////////////////////////////////////////

    private ArrayList<String> classifiers;

    /**
     * Selected classifier name.
     */
    private String selectedClassifier;
    /**
     * The accuracy of the currently selected model.
     * Is -1 if it hasn't been calculated yet.
     */
    private float accuracy = -1;

    /**
     * gets the list of classifier names.
     *
     * @return a list of strings representing classifier names
     */
    public List<String> getClassifiers() {
        return classifiers;
    }

    /**
     * Adds a classifier to the existing list and notifies listeners.
     *
     * @param classifier the classifier name to add
     */
    public void addClassifier(String classifier) {
        this.classifiers.add(classifier);
    }

    public void initClassifiers() {
        classifiers = new ArrayList<>();
        classifiers.add("");
    }

    /**
     * Clears all classifiers and notifies listeners.
     */
    public void clearClassifiers() {
        this.classifiers.clear();
    }

    /**
     * Gets the currently selected classifier.
     *
     * @return the selected classifier name as a String
     */
    public String getSelectedClassifier() {
        return selectedClassifier;
    }

    /**
     * Sets the selected classifier.
     * Also clears the accuracy, as the model has changed.
     *
     * @param selectedClassifier the classifier name to set as selected
     */
    public void setSelectedClassifier(String selectedClassifier) {
        this.selectedClassifier = selectedClassifier;
        accuracy = -1;
    }

    /**
     * Set the accuracy of the currently selected model.
     * Set it to -1 to clear the current value.
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Get the accuracy of the currently selected model.
     * -1 means it hasn't been calculated yet.
     */
    public float getAccuracy() {
        return accuracy;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///  Resampling and canvas                                                        ///
    ///////////////////////////////////////////////////////////////////////////

    private int canvasWidth;
    private int canvasHeight;
    private double previousZoom;
    private Vector3f canvasCenter = new Vector3f(0, 0, 0);
    private OnnxModel onnxModel;
    private Sampler sampler;
    private float canvasFov;
    private int samplingResolution = 20;


    public void setCanvasSize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public double getPreviousZoom() {
        return previousZoom;
    }

    public void setPreviousZoom(double previousZoom) {
        this.previousZoom = previousZoom;
    }

    public String getActiveModel() {
        return getCurrentDir().getAbsolutePath()
            + File.separatorChar + getSelectedClassifier();
    }

    public void setCanvasCenter(Vector3f position) {
        this.canvasCenter = position;
    }

    public float getCanvasX() {
        return canvasCenter.x;
    }

    public float getCanvasAspect() {
        return (float) canvasWidth / canvasHeight;
    }

    public float getCanvasY() {
        return canvasCenter.y;
    }

    public float getCanvasZ() {
        return canvasCenter.z;
    }

    public OnnxModel getOnnxModel() {
        return onnxModel;
    }

    public void setOnnxModel(OnnxModel onnxModel) {
        this.onnxModel = onnxModel;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }

    public float getCanvasFov() {
        return canvasFov;
    }

    public void setCanvasFov(float canvasFov) {
        this.canvasFov = canvasFov;
    }

    public int getSamplingResolution() {
        return samplingResolution;
    }
}

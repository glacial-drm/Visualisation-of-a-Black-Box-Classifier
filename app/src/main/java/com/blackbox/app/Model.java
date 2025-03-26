package com.blackbox.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.blackbox.generators.DataGenerator;
import com.blackbox.generators.GlobalGenOptions;
import com.blackbox.model.PointData;
import com.google.protobuf.GeneratedMessage.Builder;

public class Model {
    /**
     * List of classifier names available in the ComboBox.
     */
    private File currentDir;
    private ArrayList<String> classifiers;

    /**
     * The global options for all the training data generators.
     */
    private GlobalGenOptions genOptions = new GlobalGenOptions();
    private int selectedGenerator;
    private DataGenerator[] generators;

    private int selectedTrainingRequest = 0;
    private Builder<?>[] trainingRequests;

    private PointData[] trainingData;

    public void setTrainingData(PointData[] data) {
        trainingData = data;
    }

    public PointData[] getTrainingData() {
        return trainingData;
    }

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

    /**
     * Selected classifier name.
     */
    private String selectedClassifier;

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
     * Sets the selected classifier and notifies listeners.
     *
     * @param selectedClassifier the classifier name to set as selected
     */
    public void setSelectedClassifier(String selectedClassifier) {
        this.selectedClassifier = selectedClassifier;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File newDir) {
        currentDir = newDir;
    }
}



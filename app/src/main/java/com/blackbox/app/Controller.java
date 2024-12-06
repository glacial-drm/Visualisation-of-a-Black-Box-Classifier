package com.blackbox.app;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.blackbox.graph.events.*;
import com.blackbox.model.OnnxModel;
import com.blackbox.model.PointData;
import com.blackbox.model.Sampler;

public class Controller {
    /** The model. */
    private Model model;

    /**
     * A message queue going to the {@link com.blackbox.graph.Canvas Canvas}.
     *
     * Messages passed here will be processed in the canvas render loop.
     */
    public ConcurrentLinkedQueue<CanvasEvent> eventQueue = new ConcurrentLinkedQueue<>();

    /**
     * Constructor for the Controller.
     * @param model the model instance to work with
     */
    public Controller(Model model) {
        this.model = model;

        // Register a listener to update the view when the model changes
        model.addChangeListener(voidParam -> onModelChanged());
    }

    /**
     * Send an event to the {@link com.blackbox.graph.Canvas Canvas}.
     *
     * @param event The event to send
     */
    private void sendEvent(CanvasEvent event) {
        eventQueue.add(event);
    }

    /**
     * Send the {@link com.blackbox.graph.events.PointsLoaded PointsLoaded} event.
     *
     * @param point The point data.
     */
    public void pointLoaded(PointData point) {
        sendEvent(new PointsLoaded(point));
    }

    /**
     * Send the {@link com.blackbox.graph.events.StopEvent StopEvent} event.
     */
    public void stop() {
        // Called with true to also kill this thread
        // May be changed in the future when windows can be open independently.
        sendEvent(new StopEvent(true));
    }

    /** Get the message. */
    public String getMessage() {
        return model.message;
    }

    // Other getters
    public String[] getMenuList() {return model.menuList;}
    public String[] getFileList() {return model.fileList;}

    /**
     * gets the list of classifier names.
     * @return a list of strings representing classifier names
     */
    public List<String> getClassifiers(){
        return model.getClassifiers();
    }

    /**
     * Sets a new list of classifiers and notifies listeners.
     * @param classifiers a new List of classifier names as Strings
     */
    public void setClassifiers(List<String> classifiers) {
        model.setClassifiers(classifiers);
    }

    /**
     * Adds a classifier to the existing list and notifies listeners.
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


    public void setSelectedClassifier(String classifier) {
        model.setSelectedClassifier(classifier);
    }

    /**
     * gets navigation details for display in the navigation section.
     * @return a string with navigation instructions or information
     */
    public String getNavigationDetails(){
        return model.getNavigationDetails();
    }

    /**
     * Sets new navigation details and notifies listeners.
     * @param navigationDetails the new navigation details as a String
     */
    public void setNavigationDetails(String navigationDetails) {
        model.setNavigationDetails(navigationDetails);
    }

    /**
     * Handle the selection of a classifier from the ComboBox.
     * @param selectedClassifier the selected classifier name
     */
    public void onClassifierSelected(String selectedClassifier) {
        System.out.println("Selected classifier: " + selectedClassifier);
    }

    /**
     * Responds to model changes.
     */
    private void onModelChanged() {
        System.out.println("Model has changed. Notify the view to update components.");
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

        // Total numbers of points will be resolution * resolution
        int resolution = 30;
        // Input data will start be from -range .. +range
        float range = 2.0f;

        float pointGap = range * 2f / (float) (resolution - 1);
        // Needed for the outer edge
        float epsilon = 0.00001f;

        float[][] input = new float[resolution * resolution][2];

        int index = 0;
        for (float x = -range; x <= range + epsilon; x += pointGap) {
            for (float y = -range; y <= range + epsilon; y += pointGap) {
                input[index][0] = x;
                input[index][1] = y;
                index++;
            }
        }

        // Get output

        List<PointData> points = sampler.sampleBC(input);

        for (PointData point : points) {
            pointLoaded(point);
        }

        onnx.endSession();
    }

    public String getSelectedClassifier() {
        return model.getSelectedClassifier();
    }

    public void loadFolder() {
        System.out.println("Loading files");
    }
}

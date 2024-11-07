package com.blackbox.app;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.blackbox.graph.events.CanvasEvent;
import com.blackbox.graph.events.PointsLoaded;

public class Controller {
    /** The model. */
    private Model model;

    private String[] menuList = {"File", "Edit", "View", "Tools", "Import", "Export", "Help"};

    /**
     * A message queue going to the {@link com.blackbox.graph.Canvas Canvas}.
     *
     * Messages passed here will be processed in the canvas render loop.
     */
    public ConcurrentLinkedQueue<CanvasEvent> eventQueue = new ConcurrentLinkedQueue<>();

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
     * @param data For now is just an example, will be points in the future
     */
    public void pointsLoaded(int data) {
        sendEvent(new PointsLoaded(data));
    }

    Controller(Model model) {
        this.model = model;
    }

    /** Get the message. */
    public String getMesssge() {
        return model.message;
    }

    // Other getters
    public String[] getMenuList() {return menuList;}

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
}

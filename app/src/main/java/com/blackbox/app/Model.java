package com.blackbox.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Feel free to change/enhance any of the code
 * - Salameh Alfasatleh / psysa21
 */
public class Model {
    /** The message. (window title)*/
    public String message = "BlackBox";

    public String[] menuList = {"File", "Edit", "View", "Tools", "Help"};
    public String[] fileList = {"Import Classifier", "Export (placeholder)"};

    /** List of classifier names available in the ComboBox. */
    private List<String> classifiers = Arrays.asList("Classifier A", "Classifier B", "Classifier C");

    /** navigation details shown in the navigation section. */
    private String navigationDetails = "Navigate through points to classify data.";

    /** Selected classifier name. */
    private String selectedClassifier;

    private List<Consumer<Void>> changeListeners = new ArrayList<>();


    /**
     * gets the list of classifier names.
     * @return a list of strings representing classifier names
     */
    public List<String> getClassifiers() {
        return classifiers;
    }

    /**
     * Sets a new list of classifiers and notifies listeners.
     * @param classifiers a new List of classifier names as Strings
     */
    public void setClassifiers(List<String> classifiers) {
        this.classifiers = new ArrayList<>(classifiers);
        notifyChangeListeners();
    }

    /**
     * Adds a classifier to the existing list and notifies listeners.
     * @param classifier the classifier name to add
     */
    public void addClassifier(String classifier) {
        this.classifiers.add(classifier);
        notifyChangeListeners();
    }

    /**
     * Removes a classifier from the list and notifies listeners.
     * @param classifier the classifier name to remove
     */
    public void removeClassifier(String classifier) {
        this.classifiers.remove(classifier);
        notifyChangeListeners();
    }

    /**
     * Clears all classifiers and notifies listeners.
     */
    public void clearClassifiers() {
        this.classifiers.clear();
        notifyChangeListeners();
    }

    /**
     * Gets the currently selected classifier.
     * @return the selected classifier name as a String
     */
    public String getSelectedClassifier() {
        return selectedClassifier;
    }

    /**
     * Sets the selected classifier and notifies listeners.
     * @param selectedClassifier the classifier name to set as selected
     */
    public void setSelectedClassifier(String selectedClassifier) {
        this.selectedClassifier = selectedClassifier;
        notifyChangeListeners();
    }

    /**
     * gets navigation details for display in the navigation section.
     * @return a string with navigation instructions or information
     */
    public String getNavigationDetails() {
        return navigationDetails;
    }

    /**
     * Sets new navigation details and notifies listeners.
     * @param navigationDetails the new navigation details as a String
     */
    public void setNavigationDetails(String navigationDetails) {
        this.navigationDetails = navigationDetails;
        notifyChangeListeners();
    }

    /**
     * Adds a listener to be notified of changes in the model.
     * @param listener a Consumer that accepts a Void parameter, triggered on changes
     */
    public void addChangeListener(Consumer<Void> listener) {
        changeListeners.add(listener);
    }

    /**
     * Notifies all registered listeners of a change in the model.
     */
    private void notifyChangeListeners() {
        for (Consumer<Void> listener : changeListeners) {
            listener.accept(null);
        }
    }
}



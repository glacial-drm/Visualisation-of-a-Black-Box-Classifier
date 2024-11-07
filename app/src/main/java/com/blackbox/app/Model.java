package com.blackbox.app;

import java.util.Arrays;
import java.util.List;

public class Model {
    /** The message. (window title)*/
    public String message = "BlackBox";

    /** List of classifier names available in the ComboBox. */
    private List<String> classifiers = Arrays.asList("Classifier A", "Classifier B", "Classifier C");

    /** navigation details shown in the navigation section. */
    private String navigationDetails = "Navigate through points to classify data.";

    /**
     * gets the list of classifier names.
     * @return a list of strings representing classifier names
     */
    public List<String> getClassifiers() {
        return classifiers;
    }

    /**
     * gets navigation details for display in the navigation section.
     * @return a string with navigation instructions or information
     */
    public String getNavigationDetails() {
        return navigationDetails;
    }

}

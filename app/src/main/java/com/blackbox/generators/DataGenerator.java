package com.blackbox.generators;

import com.blackbox.model.PointData;
import javafx.scene.Node;

import java.util.Random;
import java.util.function.Consumer;

/**
 * An abstract class to generate training data.
 * <p>
 * Implementers also need to generate a UI to change the inputs.
 */
public abstract class DataGenerator {
    protected static Random rng = new Random();
    protected Consumer<DataGenerator> onChange;

    public void setOnChange(Consumer<DataGenerator> onChange) {
        this.onChange = onChange;
    }

    public abstract Node createNode();

    public abstract PointData[] buildData(GlobalGenOptions options);
}

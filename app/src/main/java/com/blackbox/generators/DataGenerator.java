package com.blackbox.generators;

import java.util.Random;
import java.util.function.Consumer;

import com.blackbox.model.PointData;

import javafx.scene.Node;

public abstract class DataGenerator {
    protected static Random rng = new Random();
    protected Consumer<DataGenerator> onChange;

    public void setOnChange(Consumer<DataGenerator> onChange) {
        this.onChange = onChange;
    } 

    public abstract Node createNode();

    public abstract PointData[] buildData(GlobalGenOptions options);
}

package com.blackbox.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.blackbox.model.PointData;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * A training data generator to generate a triangle separating the classes.
 *
 * Points are distributed within a triangular region.
 */
public class TriangleGenerator extends DataGenerator {
    private VBox box;
    private float base = 1f;
    private float height = 1f;

    @Override
    public Node createNode() {
        box = new VBox();

        box.getChildren().add(new Label("Base"));
        TextField baseInput = new TextField(Float.toString(base));
        baseInput.textProperty().addListener((_observer, _old, value) -> {
            try {
                base = Float.parseFloat(value);
                onChange.accept(this);
            } catch (Exception e) {}
        });
        box.getChildren().add(baseInput);

        box.getChildren().add(new Label("Height"));
        TextField heightInput = new TextField(Float.toString(height));
        heightInput.textProperty().addListener((_observer, _old, value) -> {
            try {
                height = Float.parseFloat(value);
                onChange.accept(this);
            } catch (Exception e) {}
        });
        box.getChildren().add(heightInput);

        return box;
    }

    private static float[] genTrianglePoint(float base, float height) {
        float x = rng.nextFloat(0, base);
        float y = rng.nextFloat(0, height * (1 - (x / base)));
        return new float[] { x, y };
    }

    @Override
    public PointData[] buildData(GlobalGenOptions options) {
        PointData[] data = new PointData[options.getCount()];

        for (int i = 0; i < options.getCount(); i++) {
            float[] point = genTrianglePoint(base, height);
            int label = (point[1] <= height * (1 - (point[0] / base))) ? 1 : 0;

            data[i] = new PointData(point[0], point[1], 0, label);
        }

        return data;
    }
}

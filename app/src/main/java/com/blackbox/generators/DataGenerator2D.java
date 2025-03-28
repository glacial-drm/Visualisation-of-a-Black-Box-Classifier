package com.blackbox.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.blackbox.model.PointData;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * An abstract class to generate 2D data.
 *
 * This handles creating the points, and toggling quadrants.
 * Implementers just need to handle the created labels.
 */
public abstract class DataGenerator2D extends DataGenerator {
    private boolean tl = true;
    private boolean tr = true;
    private boolean bl = true;
    private boolean br = true;

    @Override
    public int getDimension() { return 2; }

    private void newCheckBox(VBox box, boolean initial, Consumer<Boolean> consumer) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(initial);
        checkBox.selectedProperty().addListener((_observer, _old, value) -> {
            consumer.accept(value);
            onChange.accept(this);
        });
        box.getChildren().add(checkBox);
    }

    @Override
    public Node createNode() {
        VBox box = new VBox();

        box.getChildren().add(new Label("Quadrants"));
        newCheckBox(box, tl, b -> tl = b);
        newCheckBox(box, tr, b -> tr = b);
        newCheckBox(box, bl, b -> bl = b);
        newCheckBox(box, br, b -> br = b);

        return box;
    }

    private static float[] genQuadrant(float range, float[] offsets) {
        float x = rng.nextFloat(-range / 2f, range / 2f) + offsets[0];
        float y = rng.nextFloat(-range / 2f, range / 2f) + offsets[1];
        return new float[] { x, y };
    }

    @Override
    public PointData[] buildData(GlobalGenOptions options) {
        PointData[] data = new PointData[options.getCount()];

        List<float[]> quadrantOffsets = new ArrayList<>();
        if (tl) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, options.getRange() / 2f }); }
        if (tr) { quadrantOffsets.add(new float[] { options.getRange() / 2f, options.getRange() / 2f }); }
        if (bl) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, -options.getRange() / 2f }); }
        if (br) { quadrantOffsets.add(new float[] { options.getRange() / 2f, -options.getRange() / 2f }); }

        for (int i = 0; i < options.getCount(); i++) {
            float[] point = genQuadrant(options.getRange(), quadrantOffsets.get(rng.nextInt(quadrantOffsets.size())));
            int label = getLabel(point[0], point[1]);

            data[i] = new PointData(point, label);
        }

        return data;
    }

    abstract protected int getLabel(float x, float y);
}

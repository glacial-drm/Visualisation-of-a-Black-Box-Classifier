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
 * An abstract class to generate 3D data.
 *
 * This handles creating the points, and toggling quadrants.
 * Implementers just need to handle the created labels.
 */
public abstract class DataGenerator3D extends DataGenerator {
    private boolean frt = true;
    private boolean flt = true;
    private boolean blt = true;
    private boolean brt = true;
    private boolean frb = true;
    private boolean flb = true;
    private boolean brb = true;
    private boolean blb = true;

    @Override
    public int getDimension() { return 3; }

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
        newCheckBox(box, frt, b -> frt = b);
        newCheckBox(box, flt, b -> flt = b);
        newCheckBox(box, blt, b -> blt = b);
        newCheckBox(box, brt, b -> brt = b);
        newCheckBox(box, frb, b -> frb = b);
        newCheckBox(box, flb, b -> flb = b);
        newCheckBox(box, brb, b -> brb = b);
        newCheckBox(box, blb, b -> blb = b);

        return box;
    }

    private static float[] genQuadrant(float range, float[] offsets) {
        float x = rng.nextFloat(-range / 2f, range / 2f) + offsets[0];
        float y = rng.nextFloat(-range / 2f, range / 2f) + offsets[1];
        float z = rng.nextFloat(-range / 2f, range / 2f) + offsets[2];
        return new float[] { x, y, z };
    }

    @Override
    public PointData[] buildData(GlobalGenOptions options) {
        PointData[] data = new PointData[options.getCount()];

        List<float[]> quadrantOffsets = new ArrayList<>();
        if (frt) { quadrantOffsets.add(new float[] { options.getRange() / 2f, options.getRange() / 2f, options.getRange() / 2f }); }
        if (flt) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, options.getRange() / 2f, options.getRange() / 2f }); }
        if (blt) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, -options.getRange() / 2f, options.getRange() / 2f }); }
        if (brt) { quadrantOffsets.add(new float[] { options.getRange() / 2f, -options.getRange() / 2f, options.getRange() / 2f }); }
        if (frb) { quadrantOffsets.add(new float[] { options.getRange() / 2f, options.getRange() / 2f, -options.getRange() / 2f }); }
        if (flb) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, options.getRange() / 2f, -options.getRange() / 2f }); }
        if (brb) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, -options.getRange() / 2f, -options.getRange() / 2f }); }
        if (blb) { quadrantOffsets.add(new float[] { options.getRange() / 2f, -options.getRange() / 2f, -options.getRange() / 2f }); }

        for (int i = 0; i < options.getCount(); i++) {
            float[] point = genQuadrant(options.getRange(), quadrantOffsets.get(rng.nextInt(quadrantOffsets.size())));
            int label = getLabel(point[0], point[1], point[2]);

            data[i] = new PointData(point, label);
        }

        return data;
    }

    abstract protected int getLabel(float x, float y, float z);
}

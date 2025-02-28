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
 * A training data generator to generate a circle separating the classes.
 *
 * Quadrants are toggleable to test incomplete data.
 */
public class CircleGenerator extends DataGenerator {
    private VBox box;
    private float radius = 1f;
    private boolean tl = true;
    private boolean tr = true;
    private boolean bl = true;
    private boolean br = true;

    @Override
    public int getDimension() { return 2; }

    private void newCheckBox(boolean initial, Consumer<Boolean> consumer) {
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
        box = new VBox();

        box.getChildren().add(new Label("Radius"));
        TextField radiusInput = new TextField(Float.toString(radius));
        radiusInput.textProperty().addListener((_observer, _old, value) -> {
            try {
                radius = Float.parseFloat(value);
                onChange.accept(this);
            } catch (Exception e) {}
        });
        box.getChildren().add(radiusInput);

        box.getChildren().add(new Label("Quadrants"));
        newCheckBox(tl, b -> tl = b);
        newCheckBox(tr, b -> tr = b);
        newCheckBox(bl, b -> bl = b);
        newCheckBox(br, b -> br = b);

        return box;
    }

    private static float[] genQuadrant(float range, float[] offsets) {
        float x = rng.nextFloat(-range / 2f, range / 2f) + offsets[0];
        float y = rng.nextFloat(-range / 2f, range / 2f) + offsets[1];
        return new float[] { x, y };
    }

    @Override
    public PointData[] buildData(GlobalGenOptions options) {
        double radius_2 = Math.pow(radius, 2);
        PointData[] data = new PointData[options.getCount()];

        List<float[]> quadrantOffsets = new ArrayList<>();
        if (tl) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, options.getRange() / 2f }); }
        if (tr) { quadrantOffsets.add(new float[] { options.getRange() / 2f, options.getRange() / 2f }); }
        if (bl) { quadrantOffsets.add(new float[] { -options.getRange() / 2f, -options.getRange() / 2f }); }
        if (br) { quadrantOffsets.add(new float[] { options.getRange() / 2f, -options.getRange() / 2f }); }

        for (int i = 0; i < options.getCount(); i++) {
            float[] point = genQuadrant(options.getRange(), quadrantOffsets.get(rng.nextInt(quadrantOffsets.size())));
            int label = Math.pow((double)point[0], 2) + Math.pow((double)point[1], 2) >= radius_2 ? 0 : 1;

            data[i] = new PointData(point[0], point[1], 0, label);
        }

        return data;
    }

}

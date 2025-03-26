package com.blackbox.generators;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * A training data generator to generate a circle separating the classes.
 */
public class CubeGenerator extends DataGenerator3D {
    private float radius = 1f;

    @Override
    public Node createNode() {
        VBox box = new VBox();
        box.getChildren().add(super.createNode());

        box.getChildren().add(new Label("Radius"));
        TextField radiusInput = new TextField(Float.toString(radius));
        radiusInput.prefWidthProperty().bind(box.widthProperty());
        radiusInput.textProperty().addListener((_observer, _old, value) -> {
            try {
                radius = Float.parseFloat(value);
                onChange.accept(this);
            } catch (Exception e) {}
        });
        box.getChildren().add(radiusInput);

        return box;
    }

    @Override
    protected int getLabel(float x, float y, float z) {
        double distance = Math.pow((double)x, 2)
            + Math.pow((double)y, 2);
        return distance >= Math.pow(radius, 2) ? 0 : 1;
    }
}

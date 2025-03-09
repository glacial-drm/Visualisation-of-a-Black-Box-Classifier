package com.blackbox.generators;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Stores the training generation options
 * that are shared between the different generators.
 *
 * Creates a UI to modify itself.
 *
 * Use {@link #setOnChange()} to regenerate
 * the training data when the inputs are changed.
 */
public class GlobalGenOptions {
    private Consumer<GlobalGenOptions> onChange;

    private int count = 10_000;
    private float range = 3f;

    public void setOnChange(Consumer<GlobalGenOptions> onChange) {
        this.onChange = onChange;
    }

    public int getCount() {
        return count;
    }

    public float getRange() {
        return range;
    }

    public Node createNode() {
        VBox box = new VBox();

        TextField countInput = new TextField(Integer.toString(count));
        countInput.prefWidthProperty().bind(box.widthProperty());
        countInput.textProperty().addListener((_observer, _old, value) -> {
          try {
            count = Integer.parseInt(value);
            onChange.accept(this);
          } catch (Exception e) {}
        });
        box.getChildren().add(countInput);

        TextField rangeInput = new TextField(Float.toString(range));
        rangeInput.prefWidthProperty().bind(box.widthProperty());
        rangeInput.textProperty().addListener((_observer, _old, value) -> {
          try {
            range = Float.parseFloat(value);
            onChange.accept(this);
          } catch (Exception e) {}
        });
        box.getChildren().add(rangeInput);

        return box;
    }
}

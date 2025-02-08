package com.blackbox.generators;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
        countInput.textProperty().addListener((_observer, _old, value) -> {
          try {
            count = Integer.parseInt(value);
            onChange.accept(this);
          } catch (Exception e) {}
        });
        box.getChildren().add(countInput);

        TextField rangeInput = new TextField(Float.toString(range));
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

package com.blackbox.generators;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;

/**
 * A training data generator to generate a shape based on a given expression.
 */
public class ExprGenerator2D extends DataGenerator2D {
    private String exprStr = "x < 0";
    private Expression expr = new Expression(exprStr);

    @Override
    public Node createNode() {
        VBox box = new VBox();
        box.getChildren().add(super.createNode());

        box.getChildren().add(new Label("Expression"));
        TextField exprInput = new TextField(exprStr);
        exprInput.prefWidthProperty().bind(box.widthProperty());
        exprInput.textProperty().addListener((_observer, _old, value) -> {
            exprStr = value;
            expr = new Expression(exprStr);
            onChange.accept(this);
        });
        box.getChildren().add(exprInput);

        return box;
    }

    @Override
    protected int getLabel(float x, float y) {
        EvaluationValue result;
        try {
            result = expr
                .with("x", x)
                .with("y", y)
                .evaluate();
        } catch (Exception e) {
            return 0;
        }

        return result.getBooleanValue() ? 1 : 0;
    }
}

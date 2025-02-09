package com.blackbox.app.ui_elements;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static com.blackbox.app.View.mode;

public class UI_Button extends Button {

    //Default settings from previous implementation
    public UI_Button(String text) {
        super(text);

        updateModeElements();
        setMaxWidth(Double.MAX_VALUE);
        setPrefHeight(50);
    }

    public UI_Button(String text, double dx, double dy) {
        super(text);

        updateModeElements();
        setMaxWidth(dx);
        setPrefHeight(dy);
    }

    public void updateModeElements(){
        setFont(Font.font("Arial", FontWeight.BOLD, 20));
        if(mode == UI_Mode.DARK){
            setTextFill(Color.PURPLE);
            setStyle("-fx-border-color: #1e1e1e;");

            this.styleProperty().bind(Bindings.when(this.hoverProperty())
                    .then("-fx-background-color: #1e1e1e;")
                    .otherwise("-fx-background-color: #2c2c2c"));
        }
        else if(mode == UI_Mode.LIGHT){
            setTextFill(Color.PURPLE);
            setStyle("-fx-background-color: #2c2c2c; -fx-border-color: #1e1e1e;");

            this.styleProperty().bind(Bindings.when(this.hoverProperty())
                    .then("-fx-background-color: #1e1e1e;")
                    .otherwise("-fx-background-color: #2c2c2c"));
        }
    }

}

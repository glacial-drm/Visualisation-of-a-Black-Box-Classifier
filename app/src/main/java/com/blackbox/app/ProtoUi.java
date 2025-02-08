package com.blackbox.app;

import java.util.List;
import java.util.function.Function;

import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.Descriptors.FieldDescriptor;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProtoUi {
    private VBox box;
    private Builder<?> builder;

    public ProtoUi(Builder<?> builder) {
        this.builder = builder;
        box = new VBox();
    }

    private void addNode(Node node) {
        box.getChildren().add(node);
    }

    private <T, U> void addListener(FieldDescriptor field, ReadOnlyProperty<T> property, Function<T, U> process) {
        property.addListener((_observable, _old, value) -> {
            try {
                U processed = process.apply(value);
                builder.setField(field, processed);
            } catch (Exception e) {}
            System.out.println(builder.toString());
        });
    }

    private <T> void addListener(FieldDescriptor field, ReadOnlyProperty<T> property) {
        addListener(field, property, x -> x);
    }

    public Node build() {
        List<FieldDescriptor> fields = builder.getDescriptorForType().getFields();

        for (var field : fields) {
            addNode(new Label(field.getName()));

            switch (field.getJavaType()) {
				case BOOLEAN: {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected((Boolean)builder.getField(field));
                    addListener(field, checkBox.selectedProperty());
                    addNode(checkBox);
                    break;
                } case STRING: {
                    TextField input = new TextField(builder.getField(field).toString());
                    addListener(field, input.textProperty());
                    addNode(input);
					break;
                } case FLOAT: {
                    TextField input = new TextField(builder.getField(field).toString());
                    addListener(field, input.textProperty(), Float::parseFloat);
                    addNode(input);
					break;
                } case DOUBLE: {
                    TextField input = new TextField(builder.getField(field).toString());
                    addListener(field, input.textProperty(), Double::parseDouble);
                    addNode(input);
					break;
                } case INT: {
                    TextField input = new TextField(builder.getField(field).toString());
                    addListener(field, input.textProperty(), Integer::parseInt);
                    addNode(input);
					break;
                } case LONG: {
                    TextField input = new TextField(builder.getField(field).toString());
                    addListener(field, input.textProperty(), Long::parseLong);
                    addNode(input);
					break;
                } case ENUM: {
                    var enumType = field.getEnumType();
                    var options = enumType.getValues();
                    ComboBox<String> combo = new ComboBox<String>();
                    combo.getSelectionModel().select(builder.getField(field).toString());
                    combo.setItems(FXCollections.observableList(options.stream().map(desc -> desc.getName()).toList()));
                    addListener(field, combo.getSelectionModel().selectedItemProperty(), enumType::findValueByName);
                    addNode(combo);
					break;
                } case MESSAGE: {
					break;
                } case BYTE_STRING: {
					break;
                } default:
					break;
            }
        }

        return box;
    }
}

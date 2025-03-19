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

/**
 * Generates UI from Protocol Buffer classes.
 */
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
        });
    }

    private <T> void addListener(FieldDescriptor field, ReadOnlyProperty<T> property) {
        addListener(field, property, x -> x);
    }

    /**
     * Adds a label to the UI.
     *
     * Converts a field name from camelCase (upper or lower)
     * to capitalised words with spaces.
     *
     * e.g. camelCase -> Camel Case
     * and CamelCase -> Camel Case
     */
    private void addLabel(String fieldName) {
        char[] fieldChars = fieldName.toCharArray();
        StringBuilder name = new StringBuilder();
        name.append(Character.toUpperCase(fieldChars[0]));

        for (int i = 1; i < fieldChars.length; i++) {
            char c = fieldChars[i];

            if (Character.isUpperCase(c)) {
                name.append(' ');
            }

            name.append(c);
        }

        addNode(new Label(name.toString()));
    }

    public Node build() {
        List<FieldDescriptor> fields = builder.getDescriptorForType().getFields();

        for (var field : fields) {
            addLabel(field.getName());

            switch (field.getJavaType()) {
				case BOOLEAN: {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected((Boolean)builder.getField(field));
                    addListener(field, checkBox.selectedProperty());
                    addNode(checkBox);
                    break;
                } case STRING: {
                    TextField input = new TextField(builder.getField(field).toString());
                    input.prefWidthProperty().bind(box.widthProperty());
                    addListener(field, input.textProperty());
                    addNode(input);
					break;
                } case FLOAT: {
                    TextField input = new TextField(builder.getField(field).toString());
                    input.prefWidthProperty().bind(box.widthProperty());
                    addListener(field, input.textProperty(), Float::parseFloat);
                    addNode(input);
					break;
                } case DOUBLE: {
                    TextField input = new TextField(builder.getField(field).toString());
                    input.prefWidthProperty().bind(box.widthProperty());
                    addListener(field, input.textProperty(), Double::parseDouble);
                    addNode(input);
					break;
                } case INT: {
                    TextField input = new TextField(builder.getField(field).toString());
                    input.prefWidthProperty().bind(box.widthProperty());
                    addListener(field, input.textProperty(), Integer::parseInt);
                    addNode(input);
					break;
                } case LONG: {
                    TextField input = new TextField(builder.getField(field).toString());
                    input.prefWidthProperty().bind(box.widthProperty());
                    addListener(field, input.textProperty(), Long::parseLong);
                    addNode(input);
					break;
                } case ENUM: {
                    var enumType = field.getEnumType();
                    var options = enumType.getValues();
                    ComboBox<String> combo = new ComboBox<String>();
                    combo.prefWidthProperty().bind(box.widthProperty());
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

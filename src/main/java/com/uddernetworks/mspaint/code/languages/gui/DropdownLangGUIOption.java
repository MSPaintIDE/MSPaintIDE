package com.uddernetworks.mspaint.code.languages.gui;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;

public class DropdownLangGUIOption extends StringLangGUIOption {

    private final List<String> options;

    public DropdownLangGUIOption(String name, String... options) {
        super(name);
        this.options = Arrays.asList(options);
    }

    @Override
    public Control getDisplay() {
        var comboBox = new JFXComboBox<String>();
        comboBox.getItems().addAll(options);
        comboBox.valueProperty().bindBidirectional(this.text);
        comboBox.getStyleClass().addAll("theme-text", "language-selection");
        GridPane.setColumnIndex(comboBox, 1);
        return comboBox;
    }
}

package com.uddernetworks.mspaint.code.languages.gui;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;

/**
 * Provides a simple boolean checkbox, with no change button.
 */
public class BooleanLangGUIOption implements LangGUIOption {

    String name;
    BooleanProperty value = new SimpleBooleanProperty();

    public BooleanLangGUIOption(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Control getDisplay() {
        var checkbox = new JFXCheckBox("");
        checkbox.styleProperty().set("-jfx-checked-color: -primary-button-color;");
        checkbox.setMnemonicParsing(false);
        checkbox.setCursor(Cursor.HAND);
        checkbox.selectedProperty().bindBidirectional(this.value);
        GridPane.setColumnIndex(checkbox, 1);

        return checkbox;
    }

    @Override
    public boolean hasChangeButton() {
        return false;
    }
}

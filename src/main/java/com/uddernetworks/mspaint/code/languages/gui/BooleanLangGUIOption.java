package com.uddernetworks.mspaint.code.languages.gui;

import com.jfoenix.controls.JFXCheckBox;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
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
    int index;

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
    public void setSetting(Object setting) {
        if (setting instanceof Boolean) {
            value.setValue((Boolean) setting);
        } else if (setting instanceof String) {
            var settingString = String.valueOf(setting);
            var isTrue = settingString.equalsIgnoreCase("true");
            if (!isTrue && !settingString.equalsIgnoreCase("false")) return;
            value.setValue(isTrue);
        }
    }

    @Override
    public Object getSetting() {
        return this.value.getValue();
    }

    @Override
    public BooleanProperty getProperty() {
        return this.value;
    }

    @Override
    public void bindValue(Option option, LanguageSettings languageSettings) {
        this.value.addListener((observable, oldValue, newValue) -> languageSettings.setSetting(option, newValue, true, false));
    }

    @Override
    public boolean hasChangeButton() {
        return false;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }
}

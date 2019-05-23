package com.uddernetworks.mspaint.code.languages.gui;

import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;

/**
 * Provides a simple String with a given name and option prompt text, with no change button.
 */
public class StringLangGUIOption implements LangGUIOption {

    String name;
    StringProperty text = new SimpleStringProperty();
    String promptText;

    public StringLangGUIOption(String name) {
        this(name, null);
    }

    public StringLangGUIOption(String name, String promptText) {
        this.name = name;
        this.promptText = promptText;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Control getDisplay() {
        var textField = new JFXTextField();
        textField.textProperty().bindBidirectional(this.text);
        textField.setPromptText(this.promptText);
        textField.getStyleClass().add("theme-text");
        GridPane.setColumnIndex(textField, 1);
        return textField;
    }

    @Override
    public void setSetting(Object setting) {
        if (setting instanceof String) text.set((String) setting);
    }

    @Override
    public <G> void bindValue(G type, LanguageSettings<G> languageSettings) {
        this.text.addListener((observable, oldValue, newValue) -> languageSettings.setSetting(type, newValue, true, false));
    }

    @Override
    public boolean hasChangeButton() {
        return false;
    }
}

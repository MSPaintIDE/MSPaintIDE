package com.uddernetworks.mspaint.gui.elements;

import com.jfoenix.controls.JFXComboBox;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class SettingComboBox extends JFXComboBox<String> {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);
    private ObjectProperty<ObservableList<String>> options;

    public SettingComboBox() {
        setStyle("-jfx-checked-color:  -primary-button-color;");
        getStyleClass().add("theme-text");

        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.setSetting(settingProperty.get(), newValue);
        });
    }

    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);

        getSelectionModel().select(SettingsManager.getSetting(getSetting(), String.class, "?"));
    }

    public ObjectProperty<ObservableList<String>> optionsProperty() {
        if (options == null) {
            this.options = new SimpleObjectProperty<>(this, "items");
        }

        return this.options;
    }

    public ObservableList<String> getOptions() {
        return options == null ? null : options.get();
    }

    public void setOptions(ObservableList<String> options) {
        optionsProperty().set(options);
        setItems(options);
    }
}
package com.uddernetworks.mspaint.main.settings.guielements;

import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SettingTextField extends JFXTextField {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);

    public SettingTextField() {
        getStyleClass().add("theme-text");
        textProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.setSetting(settingProperty.get(), newValue)));
    }

    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        setText(SettingsManager.getSetting(setting, String.class, ""));
    }
}
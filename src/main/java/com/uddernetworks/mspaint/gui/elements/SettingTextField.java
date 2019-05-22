package com.uddernetworks.mspaint.gui.elements;

import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SettingTextField extends JFXTextField {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);

    public SettingTextField() {
        getStyleClass().add("theme-text");
        textProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.getInstance().setSetting(settingProperty.get(), newValue)));
    }

    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        setText(SettingsManager.getInstance().getSetting(setting, ""));
    }
}
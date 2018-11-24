package com.uddernetworks.mspaint.main.settings.guielements;

import com.jfoenix.controls.JFXCheckBox;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SettingCheckBox extends JFXCheckBox {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);

    public SettingCheckBox() {
        selectedProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.setSetting(settingProperty.get(), newValue)));
    }

    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        setSelected(SettingsManager.getSetting(setting, Boolean.class));
    }
}
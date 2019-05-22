package com.uddernetworks.mspaint.gui.elements;

import com.jfoenix.controls.JFXCheckBox;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class SettingCheckBox extends JFXCheckBox {
    private Map<String, String> styles = new HashMap<>();

    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);
    private StringProperty realPaddingProperty = new SimpleStringProperty("");

    public SettingCheckBox() {
        styles.put("checkedColor", "-jfx-checked-color:  -primary-button-color;");
        computeStyles();
        getStyleClass().add("theme-text");
        selectedProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.getInstance().setSetting(settingProperty.get(), newValue)));
    }

    private void computeStyles() {
        StringBuffer style = new StringBuffer();
        styles.forEach((key, value) -> style.append(value));

        setStyle(style.toString());
    }


    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        setSelected(SettingsManager.getInstance().getSetting(setting, false));
    }


    public StringProperty realPaddingProperty() {
        return realPaddingProperty;
    }

    public String getRealPadding() {
        return realPaddingProperty().get();
    }

    // Normal CSS padding in the order: top right bottom left | vertical horizontal
    public void setRealPadding(String padding) {
        realPaddingProperty().set(padding);
        styles.put("padding", "-fx-padding: " + padding + ";");
        computeStyles();
    }
}
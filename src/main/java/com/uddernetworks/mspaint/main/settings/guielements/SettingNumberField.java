package com.uddernetworks.mspaint.main.settings.guielements;

import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class SettingNumberField extends HBox {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);
    private StringProperty labelProperty = new SimpleStringProperty();
    private Label label = new Label("Default");
    private NumberField numberField = new NumberField();

    public SettingNumberField() {
        getStyleClass().add("theme-text");
        label.getStyleClass().add("theme-text");
        numberField.getStyleClass().add("theme-text");


        Node spacer = getHSpacer(10);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setHgrow(numberField, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.NEVER);

        label.setPrefHeight(25);
        getChildren().add(label);
        getChildren().add(spacer);
        getChildren().add(numberField);
        numberField.textProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.setSetting(settingProperty.get(), newValue.isEmpty() ? 0 : Integer.valueOf(newValue))));
    }

    public ObjectProperty<Setting> settingProperty() {
        return this.settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        numberField.setText(SettingsManager.getSetting(setting, Integer.class).toString());
    }

    public StringProperty labelProperty() {
        return this.labelProperty;
    }

    public String getLabel() {
        return labelProperty().get();
    }

    public void setLabel(String label) {
        labelProperty().set(label);
        this.label.setText(label);
    }

    private Node getHSpacer(double width) {
        Region spacer = new Region();
        spacer.setPrefWidth(width);
        HBox.setHgrow(spacer, Priority.NEVER);
        return spacer;
    }

    public class NumberField extends JFXTextField {

        @Override
        public void replaceText(int start, int end, String text) {
            if (text.matches("[0-9]*")) {
                super.replaceText(start, end, text);
            }
        }

        @Override
        public void replaceSelection(String text) {
            if (text.matches("[0-9]*")) {
                super.replaceSelection(text);
            }
        }

    }
}
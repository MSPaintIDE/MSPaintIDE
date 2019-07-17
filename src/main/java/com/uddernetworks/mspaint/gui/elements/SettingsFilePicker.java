package com.uddernetworks.mspaint.gui.elements;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.io.File;

public class SettingsFilePicker extends HBox {
    private ObjectProperty<Setting> settingProperty = new SimpleObjectProperty<>(null);
    private ObjectProperty<ChooseOptions> optionsProperty = new SimpleObjectProperty<>(null);
    private JFXTextField textField = new JFXTextField();

    public enum ChooseOptions {
        FILES_ONLY(0), DIRECTORIES_ONLY(1), SAVE_FILE(2);

        private int id;

        ChooseOptions(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    public SettingsFilePicker() {
        setHeight(25);
        setOpaqueInsets(Insets.EMPTY);

        JFXButton browse = new JFXButton("...");
        browse.setButtonType(JFXButton.ButtonType.RAISED);
        browse.setPrefWidth(25);
        browse.setPrefHeight(25);
        browse.setStyle("-fx-text-fill:WHITE;-fx-background-color:-primary-button-color;-fx-font-size:14px;");

        textField.getStyleClass().add("theme-text");
        getChildren().add(textField);
        getChildren().add(getHSpacer(10));
        getChildren().add(browse);

        HBox.setHgrow(textField, Priority.ALWAYS);
        HBox.setHgrow(browse, Priority.NEVER);

        browse.setOnAction(event -> {
            File openAt = new File(textField.getText().trim());
            if (!openAt.exists()) openAt = new File(System.getProperty("user.home", "C:\\"));

            File finalOpenAt = FileDirectoryChooser.givenOrParentDir(openAt);

            if (getOptions() == ChooseOptions.FILES_ONLY) {
                FileDirectoryChooser.openFileSelector(chooser ->
                        chooser.setInitialDirectory(finalOpenAt), file ->
                        textField.setText(file.getAbsolutePath()));
            } else if (getOptions() == ChooseOptions.DIRECTORIES_ONLY) {
                FileDirectoryChooser.openDirectorySelector(chooser ->
                        chooser.setInitialDirectory(finalOpenAt), file ->
                        textField.setText(file.getAbsolutePath()));
            } else if (getOptions() == ChooseOptions.SAVE_FILE) {
                FileDirectoryChooser.openFileSaver(chooser ->
                        chooser.setInitialDirectory(finalOpenAt), file ->
                        textField.setText(file.getAbsolutePath()));
            }
        });

        textField.textProperty().addListener(((observable, oldValue, newValue) -> SettingsManager.getInstance().setSetting(settingProperty.get(), newValue)));
    }

    private Node getHSpacer(double width) {
        Region spacer = new Region();
        spacer.setPrefWidth(width);
        HBox.setHgrow(spacer, Priority.NEVER);
        return spacer;
    }

    public ObjectProperty<Setting> settingProperty() {
        return settingProperty;
    }

    public Setting getSetting() {
        return settingProperty().get();
    }

    public void setSetting(Setting setting) {
        settingProperty().set(setting);
        textField.setText(SettingsManager.getInstance().getSetting(setting, ""));
    }

    public ObjectProperty<ChooseOptions> optionsProperty() {
        return optionsProperty;
    }

    public ChooseOptions getOptions() {
        return optionsProperty().get();
    }

    public void setOptions(ChooseOptions options) {
        optionsProperty().set(options);
    }


}

package com.uddernetworks.mspaint.code.gui;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.settings.SettingsAccessor;
import javafx.beans.property.Property;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public interface LangGUIOption {

    /**
     * Gets the name displayed on the first column of the lang settings.
     *
     * @return The name displayed
     */
    String getName();

    /**
     * Gets the {@link Control} of the {@link LangGUIOption#getName()} method to be placed directly in the GUI.
     *
     * @return The {@link Control} of the name
     */
    default Control getTextControl() {
        var textField = new Label();
        textField.setText(getName());
        textField.getStyleClass().add("theme-text");
        return textField;
    }

    /**
     * Gets the displayed element in the second column, for examaple for a simple string option,
     * a {@link com.jfoenix.controls.JFXTextField} would be displayed.
     *
     * @return The displayed option element
     */
    Control getDisplay();

    /**
     * Sets the setting, primarily used from the method {@link SettingsAccessor#onChangeSetting(Object, Consumer)}
     *
     * @param setting The setting to set, if it is invalid for the implementation it will just continue without errors
     */
    void setSetting(Object setting);

    Object getSetting();

    Property getProperty();

    /**
     * Binds the setting's value with the given {@link LanguageSettings}.
     *
     * @param languageSettings The {@link LanguageSettings} to bind with
     */
    void bindValue(Option option, LanguageSettings languageSettings);

    /**
     * If the option has a "Change" button in the last column.
     *
     * @return If the "Change" button should be displayed
     */
    boolean hasChangeButton();

    /**
     * Invoked when the "Change" button is clicked, if the current option has one.
     */
    default void activateChangeButtonAction() {}

    void setIndex(int index);

    int getIndex();

    public void setRequirement(LangGUIOptionRequirement requirement);

    LangGUIOptionRequirement getRequirement();
}

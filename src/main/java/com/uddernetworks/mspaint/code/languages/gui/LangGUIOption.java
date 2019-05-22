package com.uddernetworks.mspaint.code.languages.gui;

import javafx.scene.control.Control;

public interface LangGUIOption {

    /**
     * Gets the name displayed on the first column of the lang settings.
     *
     * @return The name displayed
     */
    String getName();

    /**
     * Gets the displayed element in the second column, for examaple for a simple string option,
     * a {@link com.jfoenix.controls.JFXTextField} would be displayed.
     *
     * @return The displayed option element
     */
    Control getDisplay();

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
}

package com.uddernetworks.mspaint.code.gui;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import javafx.beans.property.Property;
import javafx.scene.control.Control;

public class HiddenGUIOption implements LangGUIOption {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Control getDisplay() {
        return null;
    }

    @Override
    public void setSetting(Object setting) {

    }

    @Override
    public Object getSetting() {
        return null;
    }

    @Override
    public Property getProperty() {
        return null;
    }

    @Override
    public void bindValue(Option option, LanguageSettings languageSettings) {

    }

    @Override
    public boolean hasChangeButton() {
        return false;
    }

    @Override
    public void setIndex(int index) {

    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public void setRequirement(LangGUIOptionRequirement requirement) {

    }

    @Override
    public LangGUIOptionRequirement getRequirement() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}

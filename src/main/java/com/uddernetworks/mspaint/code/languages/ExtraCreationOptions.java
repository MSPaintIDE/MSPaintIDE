package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.gui.window.CreateProjectWindow;
import com.uddernetworks.mspaint.project.PPFProject;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ExtraCreationOptions extends Stage implements Initializable {


    protected CreateProjectWindow createProjectWindow;
    protected PPFProject ppfProject;
    protected Language language;
    protected Runnable onComplete = () -> {};

    public void onComplete(CreateProjectWindow createProjectWindow, PPFProject ppfProject, Language language, Runnable onComplete) {
        this.createProjectWindow = createProjectWindow;
        this.ppfProject = ppfProject;
        this.language = language;
        this.onComplete = onComplete;
    }

    public ExtraCreationOptions showWindow() {
        show();
        return this;
    }

    public static class NoExtraOptions extends ExtraCreationOptions {
        @Override
        public void onComplete(CreateProjectWindow createProjectWindow, PPFProject ppfProject, Language language, Runnable onComplete) {
            onComplete.run();
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {

        }

        @Override
        public ExtraCreationOptions showWindow() {
            return this;
        }
    }

}

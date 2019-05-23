package com.uddernetworks.mspaint.code.languages.gui;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class FileLangGUIOption extends StringLangGUIOption {

    private boolean selectDirectories;
    private FileChooser.ExtensionFilter extensionFilter;
    private File initialDirectory;
    private String chooserTitle;
    private boolean save;

    public FileLangGUIOption(String name) {
        super(name);
    }

    public FileLangGUIOption setSelectDirectories(boolean selectDirectories) {
        this.selectDirectories = selectDirectories;
        return this;
    }

    public FileLangGUIOption setExtensionFilter(FileChooser.ExtensionFilter extensionFilter) {
        this.extensionFilter = extensionFilter;
        return this;
    }

    public FileLangGUIOption setInitialDirectory(File initialDirectory) {
        this.initialDirectory = initialDirectory;
        return this;
    }

    public FileLangGUIOption setChooserTitle(String title) {
        this.chooserTitle = title;
        return this;
    }

    public FileLangGUIOption setSave(boolean save) {
        this.save = save;
        return this;
    }

    private File getUsingDefaultDirectory() {
        if (!this.text.getValueSafe().trim().equals("")) {
            var currentFile = new File(this.text.get());
            return currentFile.getParentFile();
        } else if (this.initialDirectory != null && this.initialDirectory.isDirectory()) {
            return this.initialDirectory;
        } else {
            return new File(System.getProperty("user.home", "C:\\"));
        }
    }

    @Override
    public void setSetting(Object setting) {
        if (setting instanceof File) text.set(((File) setting).getAbsolutePath());
    }

    @Override
    public <G> void bindValue(G type, LanguageSettings<G> languageSettings) {
        this.text.addListener((observable, oldValue, newValue) -> languageSettings.setSetting(type, new File(newValue), true, false));
    }

    @Override
    public boolean hasChangeButton() {
        return true;
    }

    @Override
    public void activateChangeButtonAction() {
        if (this.selectDirectories) {
            FileDirectoryChooser.openDirectorySelector(chooser -> {
                chooser.setTitle(this.chooserTitle);
                chooser.setInitialDirectory(getUsingDefaultDirectory());
            }, file -> this.text.set(file.getAbsolutePath()));
        } else {
            Consumer<FileChooser> chooserConsumer = chooser -> {
                chooser.setTitle(this.chooserTitle);
                chooser.setInitialDirectory(getUsingDefaultDirectory());
                if (this.extensionFilter != null) chooser.setSelectedExtensionFilter(this.extensionFilter);
                if (this.initialDirectory != null && this.initialDirectory.isDirectory()) chooser.setInitialDirectory(this.initialDirectory);
            };

            if (this.save) {
                FileDirectoryChooser.openFileSaver(chooserConsumer, file -> this.text.set(file.getAbsolutePath()));
            } else {
                FileDirectoryChooser.openFileSelector(chooserConsumer, file -> this.text.set(file.getAbsolutePath()));
            }
        }
    }
}

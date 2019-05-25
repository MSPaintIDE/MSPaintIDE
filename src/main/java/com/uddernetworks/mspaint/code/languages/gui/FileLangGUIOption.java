package com.uddernetworks.mspaint.code.languages.gui;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FileLangGUIOption extends StringLangGUIOption {

    private boolean selectDirectories;
    private FileChooser.ExtensionFilter extensionFilter;
    private Supplier<File> initialDirectorySupplier;
    private String chooserTitle;
    private boolean save;

    public static Supplier<File> PPF_PARENT_DIR = () -> ProjectManager.getPPFProject().getFile().getParentFile();

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

    public FileLangGUIOption setInitialDirectory(Supplier<File> initialDirectorySupplier) {
        this.initialDirectorySupplier = initialDirectorySupplier;
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
        var initialDirectory = this.initialDirectorySupplier.get();
        if (!this.text.getValueSafe().trim().equals("")) {
            var currentFile = new File(this.text.get());
            return currentFile.getParentFile();
        } else if (initialDirectory != null && initialDirectory.isDirectory()) {
            return initialDirectory;
        } else {
            return new File(System.getProperty("user.home", "C:\\"));
        }
    }

    @Override
    public void setSetting(Object setting) {
        if (setting instanceof File) text.set(((File) setting).getAbsolutePath());
    }

    @Override
    public Object getSetting() {
        return this.text != null ? new File(this.text.getValue()) : null;
    }

    @Override
    public void bindValue(Option option, LanguageSettings languageSettings) {
        this.text.addListener((observable, oldValue, newValue) -> languageSettings.setSetting(option, new File(newValue), true, false));
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
            var initialDirectory = this.initialDirectorySupplier.get();
            Consumer<FileChooser> chooserConsumer = chooser -> {
                chooser.setTitle(this.chooserTitle);
                chooser.setInitialDirectory(getUsingDefaultDirectory());
                if (this.extensionFilter != null) chooser.setSelectedExtensionFilter(this.extensionFilter);
                if (initialDirectory != null && initialDirectory.isDirectory()) chooser.setInitialDirectory(initialDirectory);
            };

            if (this.save) {
                FileDirectoryChooser.openFileSaver(chooserConsumer, file -> this.text.set(file.getAbsolutePath()));
            } else {
                FileDirectoryChooser.openFileSelector(chooserConsumer, file -> this.text.set(file.getAbsolutePath()));
            }
        }
    }
}

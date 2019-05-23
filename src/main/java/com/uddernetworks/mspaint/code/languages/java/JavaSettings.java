package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.StringLangGUIOption;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;

import java.io.File;

public class JavaSettings extends LanguageSettings<JavaOptions> {

    protected JavaSettings() {
        super("Java");

        addOption(JavaOptions.MAIN, "", new StringLangGUIOption("Main class", "com.example.Main"), // TODO: Class selection
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "Output.jar"));

        addOption(JavaOptions.JAR, "",
                new FileLangGUIOption("Jar output")
                        .setChooserTitle("Select or create the file the compiled jar will be")
                        .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                        .setExtensionFilter(ProjectFileFilter.JAR)
                        .setSave(true));

        addOption(JavaOptions.LIBRARY_LOCATION, "", new FileLangGUIOption("Library location")
                .setChooserTitle("Select the directory containing libraries used in your program")
                .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                .setSelectDirectories(true));

        addOption(JavaOptions.OTHER_LOCATION, "", new FileLangGUIOption("Other location")
                .setChooserTitle("Select the directory containing all non-java files to be put in your program, used as a 'resources' directory")
                .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                .setSelectDirectories(true));
    }

    @Override
    protected String enumToName(JavaOptions type) {
        return type.getName();
    }

    @Override
    protected JavaOptions nameToEnum(String name) {
        return JavaOptions.fromName(name);
    }
}

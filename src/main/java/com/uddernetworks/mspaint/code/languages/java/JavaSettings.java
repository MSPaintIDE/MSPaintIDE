package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.StringLangGUIOption;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;

import java.io.File;

public class JavaSettings extends LanguageSettings<JavaOptions> {

    protected JavaSettings() {
        super("Java");
    }

    @Override
    public void initOptions() {
        addOption(JavaOptions.INPUT_DIRECTORY, "",
                new FileLangGUIOption("Input directory")
                        .setChooserTitle("Select the source directory containing your sourcecode images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("src"));

        addOption(JavaOptions.HIGHLIGHT_DIRECTORY, "",
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));

        addOption(JavaOptions.MAIN, "", new StringLangGUIOption("Main class", "com.example.Main"), // TODO: Class selection
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "Output.jar"));

        addOption(JavaOptions.JAR, "",
                new FileLangGUIOption("Jar output")
                        .setChooserTitle("Select or create the file the compiled jar will be")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.JAR)
                        .setSave(true));

        addOption(JavaOptions.CLASS_OUTPUT, "",
                new FileLangGUIOption("Class output")
                        .setChooserTitle("Select the directory the classes will compile to")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> create(new File(ProjectManager.getPPFProject().getFile().getParentFile(), "build")));


        addOption(JavaOptions.HIGHLIGHT, true, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(JavaOptions.COMPILE, true, new BooleanLangGUIOption("Compile program"), () -> true);

        addOption(JavaOptions.EXECUTE, true, new BooleanLangGUIOption("Execute program"), () -> true);

        addOption(JavaOptions.LIBRARY_LOCATION, "", new FileLangGUIOption("Library location")
                .setChooserTitle("Select the directory containing libraries used in your program")
                .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                .setSelectDirectories(true));

        addOption(JavaOptions.OTHER_LOCATION, "", new FileLangGUIOption("Other location")
                .setChooserTitle("Select the directory containing all non-java files to be put in your program, used as a 'resources' directory")
                .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
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

    @Override
    public LangGUIOptionRequirement getRequirement(JavaOptions type) {
        return type.getRequirement();
    }
}

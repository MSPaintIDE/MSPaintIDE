package com.uddernetworks.mspaint.code.languages.javascript;

import com.uddernetworks.mspaint.code.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JSSettings extends LanguageSettings {

    private static Logger LOGGER = LoggerFactory.getLogger(com.uddernetworks.mspaint.code.languages.java.JavaSettings.class);

    protected JSSettings() {
        super("JavaScript");
    }

    @Override
    public void initOptions() {
        addOption(JSOptions.INPUT_DIRECTORY,
                new FileLangGUIOption("Input directory")
                        .setChooserTitle("Select the source directory containing your sourcecode images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("src"));

        addOption(JSOptions.HIGHLIGHT_DIRECTORY,
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));

        addOption(JSOptions.RUNNING_FILE,
                new FileLangGUIOption("Running file")
                        .setChooserTitle("Select or create the png file that will be executed")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG));

        addOption(JSOptions.COMPILER_OUTPUT,
                new FileLangGUIOption("Compiler output")
                        .setChooserTitle("Select or create the png file where compiler output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "compiler.png"));

        addOption(JSOptions.PROGRAM_OUTPUT,
                new FileLangGUIOption("Program output")
                        .setChooserTitle("Select or create the png file where program output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "program.png"));

        addOption(JSOptions.HIGHLIGHT, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(JSOptions.EXECUTE, new BooleanLangGUIOption("Execute program"), () -> true);

        reload();
    }

    @Override
    protected JSOptions nameToEnum(String name) {
        return JSOptions.staticFromName(name);
    }
}

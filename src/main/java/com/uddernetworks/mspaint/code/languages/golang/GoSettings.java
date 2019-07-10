package com.uddernetworks.mspaint.code.languages.golang;

import com.uddernetworks.mspaint.code.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GoSettings extends LanguageSettings {

    private static Logger LOGGER = LoggerFactory.getLogger(GoSettings.class);

    protected GoSettings() {
        super("Go");
    }

    @Override
    public void initOptions() {
        addOption(GoOptions.INPUT_DIRECTORY,
                new FileLangGUIOption("Input directory"),
                () -> ProjectManager.getPPFProject().getFile().getParentFile());

        addOption(GoOptions.HIGHLIGHT_DIRECTORY,
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));

        addOption(GoOptions.RUNNING_FILE,
                new FileLangGUIOption("Running file")
                        .setChooserTitle("Select or create the png file that will be executed")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG));

        addOption(GoOptions.COMPILER_OUTPUT,
                new FileLangGUIOption("Compiler output")
                        .setChooserTitle("Select or create the png file where compiler output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "compiler.png"));

        addOption(GoOptions.PROGRAM_OUTPUT,
                new FileLangGUIOption("Program output")
                        .setChooserTitle("Select or create the png file where program output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "program.png"));

        addOption(GoOptions.HIGHLIGHT, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(GoOptions.COMPILE, new BooleanLangGUIOption("Compile program"), () -> true);

        addOption(GoOptions.EXECUTE, new BooleanLangGUIOption("Execute program"), () -> true);

        reload();
    }

    @Override
    protected GoOptions nameToEnum(String name) {
        return GoOptions.staticFromName(name);
    }
}

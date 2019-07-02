package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.code.languages.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.main.ProjectFileFilter;

public class PythonSettings extends LanguageSettings {

    protected PythonSettings() {
        super("Python");
    }

    @Override
    public void initOptions() {
        addOption(PythonOptions.INPUT_DIRECTORY,
                new FileLangGUIOption("Input directory")
                        .setChooserTitle("Select the source directory containing your sourcecode images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("src"));

        addOption(PythonOptions.HIGHLIGHT_DIRECTORY,
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));

        addOption(PythonOptions.RUNNING_FILE,
                new FileLangGUIOption("Running file")
                        .setChooserTitle("Select or create the png file that will be executed")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG));

        addOption(PythonOptions.COMPILER_OUTPUT,
                new FileLangGUIOption("Compiler output")
                        .setChooserTitle("Select or create the png file where compiler output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true));

        addOption(PythonOptions.PROGRAM_OUTPUT,
                new FileLangGUIOption("Program output")
                        .setChooserTitle("Select or create the png file where program output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true));

        addOption(PythonOptions.HIGHLIGHT, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(PythonOptions.EXECUTE, new BooleanLangGUIOption("Execute program"), () -> true);

        reload();
    }

    @Override
    protected Option nameToEnum(String name) {
        return PythonOptions.staticFromName(name);
    }
}

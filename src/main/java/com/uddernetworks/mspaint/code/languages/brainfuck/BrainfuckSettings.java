package com.uddernetworks.mspaint.code.languages.brainfuck;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;

public class BrainfuckSettings extends LanguageSettings<BrainfuckOptions> {

    protected BrainfuckSettings() {
        super("Brainfuck");
    }

    @Override
    public void initOptions() {
        addOption(BrainfuckOptions.INPUT_DIRECTORY, "",
                new FileLangGUIOption("Input directory")
                        .setChooserTitle("Select the source directory containing your sourcecode images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("src"));

        addOption(BrainfuckOptions.HIGHLIGHT, true, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(BrainfuckOptions.HIGHLIGHT_DIRECTORY, "",
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));
    }

    @Override
    protected String enumToName(BrainfuckOptions type) {
        return type.getName();
    }

    @Override
    protected BrainfuckOptions nameToEnum(String name) {
        return BrainfuckOptions.fromName(name);
    }

    @Override
    public LangGUIOptionRequirement getRequirement(BrainfuckOptions type) {
        return type.getRequirement();
    }
}

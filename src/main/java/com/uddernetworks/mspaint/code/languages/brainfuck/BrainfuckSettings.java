//package com.uddernetworks.mspaint.code.languages.brainfuck;
//
//import com.uddernetworks.mspaint.code.languages.LanguageSettings;
//import com.uddernetworks.mspaint.code.languages.gui.BooleanLangGUIOption;
//import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;
//import com.uddernetworks.mspaint.main.ProjectFileFilter;
//
//public class BrainfuckSettings extends LanguageSettings {
//
//    protected BrainfuckSettings() {
//        super("Brainfuck");
//    }
//
//    @Override
//    public void initOptions() {
//        addOption(BrainfuckOptions.INPUT_DIRECTORY,
//                new FileLangGUIOption("Input directory")
//                        .setChooserTitle("Select the source directory containing your sourcecode images")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setSelectDirectories(true),
//                () -> createSubOfProject("src"));
//
//        addOption(BrainfuckOptions.HIGHLIGHT, new BooleanLangGUIOption("Syntax highlight"), () -> true);
//
//        addOption(BrainfuckOptions.HIGHLIGHT_DIRECTORY,
//                new FileLangGUIOption("Highlight directory")
//                        .setChooserTitle("Select the directory to contain all highlighted code images")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setSelectDirectories(true),
//                () -> createSubOfProject("highlight"));
//
//        addOption(BrainfuckOptions.COMPILER_OUTPUT,
//                new FileLangGUIOption("Compiler output")
//                        .setChooserTitle("Select or create the png file where compiler output text will be located")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setExtensionFilter(ProjectFileFilter.PNG)
//                        .setSave(true));
//
//        addOption(BrainfuckOptions.PROGRAM_OUTPUT,
//                new FileLangGUIOption("Program output")
//                        .setChooserTitle("Select or create the png file where program output text will be located")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setExtensionFilter(ProjectFileFilter.PNG)
//                        .setSave(true));
//    }
//
//    @Override
//    protected BrainfuckOptions nameToEnum(String name) {
//        return BrainfuckOptions.staticFromName(name);
//    }
//}

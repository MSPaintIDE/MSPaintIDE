package com.uddernetworks.mspaint.code.languages.java.buildsystem.gradle;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.gui.BooleanLangGUIOption;
import com.uddernetworks.mspaint.code.gui.DropdownLangGUIOption;
import com.uddernetworks.mspaint.code.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.gui.HiddenGUIOption;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.java.JavaLangOptions;
import com.uddernetworks.mspaint.code.languages.java.JavaOptions;
import com.uddernetworks.mspaint.code.languages.java.JavaSettings;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GradleSettings extends JavaSettings {

    private static Logger LOGGER = LoggerFactory.getLogger(GradleSettings.class);

    public GradleSettings() {
        super();
    }

    public GradleSettings(LanguageSettings settings) {
        super(settings);
    }

    @Override
    public void initOptions() {
        addOption(JavaLangOptions.INPUT_DIRECTORY,
                new FileLangGUIOption("Input directory")
                        .setChooserTitle("Select the source directory containing your sourcecode images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("src"));

        addOption(JavaLangOptions.HIGHLIGHT_DIRECTORY,
                new FileLangGUIOption("Highlight directory")
                        .setChooserTitle("Select the directory to contain all highlighted code images")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setSelectDirectories(true),
                () -> createSubOfProject("highlight"));

//        addOption(JavaLangOptions.MAIN, new StringLangGUIOption("Main class", "com.example.Main"), // TODO: Class selection
//                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "Output.jar"));

//        addOption(JavaLangOptions.JAR,
//                new FileLangGUIOption("Jar output")
//                        .setChooserTitle("Select or create the file the compiled jar will be")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setExtensionFilter(ProjectFileFilter.JAR)
//                        .setSave(true),
//                () ->  new File(ProjectManager.getPPFProject().getFile().getParentFile(), "Output.jar"));

//        addOption(JavaLangOptions.CLASS_OUTPUT,
//                new FileLangGUIOption("Class output")
//                        .setChooserTitle("Select the directory the classes will compile to")
//                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
//                        .setSelectDirectories(true),
//                () -> create(new File(ProjectManager.getPPFProject().getFile().getParentFile(), "build")));

        addOption(JavaLangOptions.CLASS_OUTPUT, new HiddenGUIOption(),
                () -> create(new File(ProjectManager.getPPFProject().getFile().getParentFile(), "build")));

        addOption(JavaLangOptions.COMPILER_OUTPUT,
                new FileLangGUIOption("Compiler output")
                        .setChooserTitle("Select or create the png file where compiler output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "compiler.png"));

        addOption(JavaLangOptions.PROGRAM_OUTPUT,
                new FileLangGUIOption("Program output")
                        .setChooserTitle("Select or create the png file where program output text will be located")
                        .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                        .setExtensionFilter(ProjectFileFilter.PNG)
                        .setSave(true),
                () -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "program.png"));

        addOption(JavaLangOptions.JAVA_VERSION,
                new DropdownLangGUIOption("Java Version", "Java 8", "Java 9", "Java 10", "Java 11", "Java 12", "Java 13"),
                () -> {
                    var output = Commandline.runCommand("java", "--version");
                    if (!output.contains(" ")) return "Java 11";
                    var version = "Java " + output.split("\\s+")[1].split("\\.")[0];
                    LOGGER.info("Current version of Java is {}", version);
                    return version;
                });

        addOption(JavaLangOptions.HIGHLIGHT, new BooleanLangGUIOption("Syntax highlight"), () -> true);

        addOption(JavaLangOptions.COMPILE, new BooleanLangGUIOption("Compile program"), () -> true);

        addOption(JavaLangOptions.EXECUTE, new BooleanLangGUIOption("Execute program"), () -> true);

        addOption(JavaLangOptions.LIBRARY_LOCATION, new FileLangGUIOption("Library location")
                .setChooserTitle("Select the directory containing libraries used in your program")
                .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                .setSelectDirectories(true));

        addOption(JavaLangOptions.OTHER_LOCATION, new FileLangGUIOption("Other location")
                .setChooserTitle("Select the directory containing all non-java files to be put in your program, used as a 'resources' directory")
                .setInitialDirectory(FileLangGUIOption.PPF_PARENT_DIR)
                .setSelectDirectories(true));

        reload();
    }

    @Override
    protected JavaOptions nameToEnum(String name) {
        return JavaLangOptions.staticOptionalFromName(name).orElseGet(() -> GradleOptions.staticFromName(name));
    }
}

package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.languages.gui.FileLangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.LangGUIOption;
import com.uddernetworks.mspaint.code.languages.gui.StringLangGUIOption;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public enum JavaOptions {
    MAIN("classLocation", () -> new StringLangGUIOption("Main class", "com.example.Main")), // TODO: Class selection
    JAR("jarFile", main -> new File(ProjectManager.getPPFProject().getFile().getParentFile(), "Output.jar"), () ->
            new FileLangGUIOption("Jar output")
                    .setChooserTitle("Select or create the file the compiled jar will be")
                    .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                    .setExtensionFilter(ProjectFileFilter.JAR)
                    .setSave(true)),
    LIBRARY_LOCATION("libraryLocation", () ->
            new FileLangGUIOption("Library location")
                    .setChooserTitle("Select the directory containing libraries used in your program")
                    .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                    .setSelectDirectories(true)),
    OTHER_LOCATION("otherLocation", () ->
            new FileLangGUIOption("Other location")
                    .setChooserTitle("Select the directory containing all non-java files to be put in your program, used as a 'resources' directory")
                    .setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile())
                    .setSelectDirectories(true));

    private String name;
    private Function<Main, Object> defaultFunction;
    private LangGUIOption guiOption;

    JavaOptions(String name) {
        this(name, () -> null);
    }

    JavaOptions(String name, Supplier<LangGUIOption> supplyGUIOption) {
        this(name, i -> null, supplyGUIOption);
    }

    JavaOptions(String name, Function<Main, Object> supplyDefault, Supplier<LangGUIOption> supplyGUIOption) {
        this.name = name;
        this.defaultFunction = supplyDefault;
        this.guiOption = supplyGUIOption.get();
    }

    public String getName() {
        return name;
    }

    public LangGUIOption getGuiOption() {
        return guiOption;
    }

    public <T> T generateDefault(Main main) {
        return (T) this.defaultFunction.apply(main);
    }

    public static JavaOptions fromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
    }

    public static void generateAll(Main main) {
        Arrays.stream(values()).forEach(option -> option.generateDefault(main));
    }
}

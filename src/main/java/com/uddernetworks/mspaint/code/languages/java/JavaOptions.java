package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;

import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.*;

/**
 * All the Java language options. All options that are linked as {@link LangGUIOptionRequirement#OPTIONAL} must require
 * the {@link LanguageSettings#getSettingOptional} and other Optional-returning methods, as they may not be set.
 */
public enum JavaOptions implements Option {
    /**
     * The directory where code image files go
     * @see LangGUIOptionRequirement#REQUIRED
     */
    INPUT_DIRECTORY("inputDirectory", REQUIRED),
    /**
     * The output directory where generated highlighted code files go
     * @see LangGUIOptionRequirement#REQUIRED
     */
    HIGHLIGHT_DIRECTORY("highlightDirectory", REQUIRED),
    /**
     * The canonical name of the class to run
     * @see LangGUIOptionRequirement#REQUIRED
     */
    MAIN("classLocation", REQUIRED),
    /**
     * The jar file to output
     * @see LangGUIOptionRequirement#REQUIRED
     */
    JAR("jarFile", REQUIRED),
    /**
     * The directory where compiled .class files go
     * @see LangGUIOptionRequirement#REQUIRED
     */
    CLASS_OUTPUT("classOutput", REQUIRED),
    /**
     * The image file to output compiler information
     * @see LangGUIOptionRequirement#REQUIRED
     */
    COMPILER_OUTPUT("compilerOutput", REQUIRED),
    /**
     * The image file to output the running program's console
     * @see LangGUIOptionRequirement#REQUIRED
     */
    PROGRAM_OUTPUT("programOutput", REQUIRED),
    /**
     * A boolean to toggle the highlighting of code files
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    HIGHLIGHT("highlight", BOTTOM_DISPLAY),
    /**
     * A boolean to toggle the compilation of files
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    COMPILE("compile", BOTTOM_DISPLAY),
    /**
     * A boolean to toggle the execution of files
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    EXECUTE("execute", BOTTOM_DISPLAY),
    /**
     * The directory where .jar libraries are placed
     * @see LangGUIOptionRequirement#OPTIONAL
     */
    LIBRARY_LOCATION("libraryLocation", OPTIONAL),
    /**
     * The directory where other files to be included in the jar are located
     * @see LangGUIOptionRequirement#OPTIONAL
     */
    OTHER_LOCATION("otherLocation", OPTIONAL);

    private String name;
    private LangGUIOptionRequirement requirement;

    JavaOptions(String name, LangGUIOptionRequirement requirement) {
        this.name = name;
        this.requirement = requirement;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Option fromName(String name) {
        return staticFromName(name);
    }

    @Override
    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }

    public static JavaOptions staticFromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
    }
}

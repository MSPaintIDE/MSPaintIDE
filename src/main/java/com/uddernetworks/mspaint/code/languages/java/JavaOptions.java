package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;

import java.io.File;
import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.*;

/**
 * All the Java language options. All options that are linked as {@link LangGUIOptionRequirement#OPTIONAL} must require
 * the {@link LanguageSettings#getSettingOptional} and other Optional-returning methods, as they may not be set.
 */
public enum JavaOptions implements Option {
    /**
     * The directory where code image files go
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    INPUT_DIRECTORY("inputDirectory", File.class, REQUIRED),
    /**
     * The output directory where generated highlighted code files go
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    HIGHLIGHT_DIRECTORY("highlightDirectory", File.class, REQUIRED),
    /**
     * The canonical name of the class to run
     * @see String
     * @see LangGUIOptionRequirement#REQUIRED
     */
    MAIN("classLocation", String.class, REQUIRED),
    /**
     * The jar file to output
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    JAR("jarFile", File.class, REQUIRED),
    /**
     * The directory where compiled .class files go
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    CLASS_OUTPUT("classOutput", File.class, REQUIRED),
    /**
     * The image file to output compiler information
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    COMPILER_OUTPUT("compilerOutput", File.class, REQUIRED),
    /**
     * The image file to output the running program's console
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    PROGRAM_OUTPUT("programOutput", File.class, REQUIRED),
    /**
     * The version of Java to be used, from a dropdown selector
     * @see String
     * @see LangGUIOptionRequirement#REQUIRED
     */
    JAVA_VERSION("javaVersion", String.class, REQUIRED),
    /**
     * A boolean to toggle the highlighting of code files
     * @see Boolean
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    HIGHLIGHT("highlight", Boolean.class, BOTTOM_DISPLAY),
    /**
     * A boolean to toggle the compilation of files
     * @see Boolean
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    COMPILE("compile", Boolean.class, BOTTOM_DISPLAY),
    /**
     * A boolean to toggle the execution of files
     * @see Boolean
     * @see LangGUIOptionRequirement#BOTTOM_DISPLAY
     */
    EXECUTE("execute", Boolean.class, BOTTOM_DISPLAY),
    /**
     * The directory where .jar libraries are placed
     * @see String
     * @see LangGUIOptionRequirement#OPTIONAL
     */
    LIBRARY_LOCATION("libraryLocation", String.class, OPTIONAL),
    /**
     * The directory where other files to be included in the jar are located
     * @see String
     * @see LangGUIOptionRequirement#OPTIONAL
     */
    OTHER_LOCATION("otherLocation", String.class, OPTIONAL);

    private String name;
    private LangGUIOptionRequirement requirement;
    private Class type;

    JavaOptions(String name, Class type, LangGUIOptionRequirement requirement) {
        this.name = name;
        this.type = type;
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
    public Class getType() {
        return this.type;
    }

    @Override
    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }

    public static JavaOptions staticFromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
    }
}

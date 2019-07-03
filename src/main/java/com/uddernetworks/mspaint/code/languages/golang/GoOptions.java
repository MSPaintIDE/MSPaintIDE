package com.uddernetworks.mspaint.code.languages.golang;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.Option;

import java.io.File;
import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.*;

public enum GoOptions implements Option {
    /**
     * The directory where code image files go
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    INPUT_DIRECTORY("inputDirectory", File.class, UNMODIFIABLE),
    /**
     * The output directory where generated highlighted code files go
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    HIGHLIGHT_DIRECTORY("highlightDirectory", File.class, REQUIRED),
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
     * The file to run
     * @see File
     * @see LangGUIOptionRequirement#REQUIRED
     */
    RUNNING_FILE("runningFile", File.class, OPTIONAL),
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
    EXECUTE("execute", Boolean.class, BOTTOM_DISPLAY);

    private String name;
    private LangGUIOptionRequirement requirement;
    private Class type;

    GoOptions(String name, Class type, LangGUIOptionRequirement requirement) {
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

    public static GoOptions staticFromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(GoOptions.class, name));
    }
}

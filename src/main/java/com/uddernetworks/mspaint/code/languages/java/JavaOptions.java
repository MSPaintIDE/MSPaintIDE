package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;

import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.*;

public enum JavaOptions {
    INPUT_DIRECTORY("inputDirectory", REQUIRED),
    HIGHLIGHT_DIRECTORY("highlightDirectory", REQUIRED),
    MAIN("classLocation", REQUIRED),
    JAR("jarFile", REQUIRED),
    CLASS_OUTPUT("classOutput", REQUIRED),
    HIGHLIGHT("highlight", BOTTOM_DISPLAY),
    COMPILE("compile", BOTTOM_DISPLAY),
    EXECUTE("execute", BOTTOM_DISPLAY),
    LIBRARY_LOCATION("libraryLocation", OPTIONAL),
    OTHER_LOCATION("otherLocation", OPTIONAL);

    private String name;
    private LangGUIOptionRequirement requirement;

    JavaOptions(String name, LangGUIOptionRequirement requirement) {
        this.name = name;
        this.requirement = requirement;
    }

    public String getName() {
        return name;
    }

    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }

    public static JavaOptions fromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name + "|shit"));
    }
}

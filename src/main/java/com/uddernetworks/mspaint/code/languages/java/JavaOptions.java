package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.Option;

import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.*;

public enum JavaOptions implements Option {
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

    @Override
    public Option fromName(String name) {
        return staticFromName(name);
    }

    public static JavaOptions staticFromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
    }

    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }
}

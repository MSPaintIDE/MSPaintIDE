package com.uddernetworks.mspaint.code.languages.brainfuck;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.java.JavaOptions;

import java.util.Arrays;

import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.BOTTOM_DISPLAY;
import static com.uddernetworks.mspaint.code.LangGUIOptionRequirement.REQUIRED;

public enum BrainfuckOptions {
    INPUT_DIRECTORY("inputDirectory", REQUIRED),
    HIGHLIGHT("highlight", BOTTOM_DISPLAY),
    HIGHLIGHT_DIRECTORY("highlightDirectory", REQUIRED);

    private String name;
    private LangGUIOptionRequirement requirement;

    BrainfuckOptions(String name, LangGUIOptionRequirement requirement) {
        this.name = name;
        this.requirement = requirement;
    }

    public String getName() {
        return name;
    }

    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }

    public static BrainfuckOptions fromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name + "|shit"));
    }
}

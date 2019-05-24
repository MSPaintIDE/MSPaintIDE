package com.uddernetworks.mspaint.code.languages.brainfuck;

import java.util.Arrays;

public enum BrainfuckOptions {
    INPUT_DIRECTORY("inputDirectory", true),
    HIGHLIGHT_DIRECTORY("highlightDirectory", true);

    private String name;
    private boolean required;

    BrainfuckOptions(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public static BrainfuckOptions fromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(BrainfuckOptions.class, name));
    }
}

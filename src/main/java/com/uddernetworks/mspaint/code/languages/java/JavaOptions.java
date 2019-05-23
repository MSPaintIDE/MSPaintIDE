package com.uddernetworks.mspaint.code.languages.java;

import java.util.Arrays;

public enum JavaOptions {
    MAIN("classLocation", true),
    JAR("jarFile", true),
    LIBRARY_LOCATION("libraryLocation", false),
    OTHER_LOCATION("otherLocation", false);

    private String name;
    private boolean required;

    JavaOptions(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public static JavaOptions fromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(JavaOptions.class, name));
    }
}

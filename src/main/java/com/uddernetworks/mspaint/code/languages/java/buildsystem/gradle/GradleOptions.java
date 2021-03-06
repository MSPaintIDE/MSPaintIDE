package com.uddernetworks.mspaint.code.languages.java.buildsystem.gradle;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.java.JavaOptions;

import java.util.Arrays;

public enum GradleOptions implements JavaOptions {
    ;

    private String name;
    private LangGUIOptionRequirement requirement;
    private Class<?> type;

    GradleOptions(String name, Class<?> type, LangGUIOptionRequirement requirement) {
        this.name = name;
        this.type = type;
        this.requirement = requirement;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GradleOptions fromName(String name) {
        return staticFromName(name);
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public LangGUIOptionRequirement getRequirement() {
        return this.requirement;
    }

    public static GradleOptions staticFromName(String name) {
        return Arrays.stream(values()).filter(option -> option.name.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new EnumConstantNotPresentException(GradleOptions.class, name));
    }
}
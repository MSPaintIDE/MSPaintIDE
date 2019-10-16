package com.uddernetworks.mspaint.code.languages.java;

public enum JavaBuildSystem {
    DEFAULT("Default"),
    GRADLE("Gradle");

    private String name;

    JavaBuildSystem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

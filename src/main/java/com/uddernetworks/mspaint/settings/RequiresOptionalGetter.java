package com.uddernetworks.mspaint.settings;

public class RequiresOptionalGetter extends RuntimeException {
    public RequiresOptionalGetter(String settingString) {
        super("The setting " + settingString + " has an optional-only restriction on it, meaning it must use one of the optional-returning getters.");
    }
}
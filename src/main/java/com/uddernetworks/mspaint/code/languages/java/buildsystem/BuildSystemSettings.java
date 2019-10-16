package com.uddernetworks.mspaint.code.languages.java.buildsystem;

import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.java.JavaOptions;

import java.util.Map;
import java.util.Optional;

public interface BuildSystemSettings {
    Map<JavaOptions, Optional<Runnable>> getOptionSettings(LanguageSettings settings);
}

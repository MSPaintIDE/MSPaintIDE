package com.uddernetworks.mspaint.code.languages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageManager {

    private static Logger LOGGER = LoggerFactory.getLogger(LanguageManager.class);

    private List<Language> allLanguages = new ArrayList<>();
    private List<Language> enabledLanguages = new ArrayList<>();

    public List<Language> getAllLanguages() {
        return allLanguages;
    }

    public List<Language> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void addLanguage(Language language) {
        this.allLanguages.add(language);
    }

    // TODO: Remove warnings with option
    public void initializeLanguages() {
        this.enabledLanguages = this.allLanguages.stream()
                .filter(language -> {
                    LOGGER.info("Loading the language \"" + language.getName() + "\"");

                    if (!language.hasLSP()) {
                        LOGGER.warn("Your system does not have the LSP for {}, which is required to use the language. Go to the settings if you would like to add it or remove this warning.", language.getName());
                        return false;
                    }

                    if (language.hasRuntime()) {
                        LOGGER.warn("Your system does not have the runtime for {}. You will still be allowed to edit with the language, just not compile/execute code with it. Go to the settings if you would like to add it or remove this warning.", language.getName());
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    public Optional<Language> getLanguageFromFileExtension(String fileExtension) {
        return this.enabledLanguages.stream()
                .filter(language ->
                        Arrays.stream(language.getFileExtensions()).anyMatch(extension ->
                                extension.equalsIgnoreCase(fileExtension)))
                .findFirst();
    }

    public void reloadAllLanguages() {
        this.enabledLanguages.stream().map(Language::getLanguageSettings).forEach(LanguageSettings::reload);
    }

    public Optional<Language> getLanguageByClass(Class<?> clazz) {
        return this.enabledLanguages.stream().filter(language -> language.getClass().equals(clazz)).findFirst();
    }
}

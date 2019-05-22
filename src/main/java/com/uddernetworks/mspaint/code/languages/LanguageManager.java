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

    public LanguageManager() {

    }

    public List<Language> getAllLanguages() {
        return allLanguages;
    }

    public List<Language> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void addLanguage(Language language) {
        this.allLanguages.add(language);
    }

    public void initializeLanguages() {
        this.enabledLanguages = this.allLanguages.stream()
                .filter(language -> {
                    LOGGER.info("Loading the language \"" + language.getName() + "\"");

                    if (language.meetsRequirements()) return true;

                    LOGGER.warn("Your system does not meet the requirements for " + language.getName());
                    return false;
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
}

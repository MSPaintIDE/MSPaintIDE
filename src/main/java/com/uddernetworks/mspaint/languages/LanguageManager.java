package com.uddernetworks.mspaint.languages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageManager {

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
                    System.out.println("Loading the language \"" + language.getName() + "\"");

                    if (language.meetsRequirements()) return true;

                    System.out.println("Your system does not meet the requirements for " + language.getName());
                    return false;
                })
                .collect(Collectors.toList());
    }
}

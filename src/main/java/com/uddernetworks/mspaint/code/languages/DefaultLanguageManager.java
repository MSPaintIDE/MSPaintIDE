package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.uddernetworks.mspaint.settings.Setting.INCOMPATIBLE_LANGUAGE_ERRORS;

public class DefaultLanguageManager implements LanguageManager {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultLanguageManager.class);

    private List<Language> allLanguages = new ArrayList<>();
    private List<Language> enabledLanguages = new ArrayList<>();

    @Override
    public void addLanguage(Language language) {
        this.allLanguages.add(language);
    }

    @Override
    public void initializeLanguages() {
        var printIncompatibleMessages = SettingsManager.getInstance().<Boolean>getSetting(INCOMPATIBLE_LANGUAGE_ERRORS);
        this.enabledLanguages = this.allLanguages.stream()
                .filter(language -> {
                    LOGGER.info("Loading the language \"" + language.getName() + "\"");

                    if (!language.hasLSP()) {
                        if (printIncompatibleMessages)
                            LOGGER.warn("Your system does not have the LSP for {}, which is required to use the language. Go to the settings if you would like to add it or remove this warning.", language.getName());
                        return false;
                    }

                    if (!language.hasRuntime() && printIncompatibleMessages) {
                        LOGGER.warn("Your system does not have the runtime for {}. You will still be allowed to edit with the language, just not compile/execute code with it. Go to the settings if you would like to add it or remove this warning.", language.getName());
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void reloadAllLanguages() {
        this.enabledLanguages.stream().map(Language::getLanguageSettings).forEach(LanguageSettings::reload);
    }

    @Override
    public Optional<Language> getLanguageFromFileExtension(String fileExtension) {
        return this.enabledLanguages.stream()
                .filter(language ->
                        Arrays.stream(language.getFileExtensions()).anyMatch(extension ->
                                extension.equalsIgnoreCase(fileExtension)))
                .findFirst();
    }

    @Override
    public Optional<Language> getLanguageByClass(Class<?> clazz) {
        return this.allLanguages.stream().filter(language -> language.getClass().equals(clazz)).findFirst();
    }

    @Override
    public List<Language> getAllLanguages() {
        return allLanguages;
    }

    @Override
    public List<Language> getEnabledLanguages() {
        return enabledLanguages;
    }
}

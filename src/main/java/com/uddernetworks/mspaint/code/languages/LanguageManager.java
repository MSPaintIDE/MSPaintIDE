package com.uddernetworks.mspaint.code.languages;

import java.util.List;
import java.util.Optional;

public interface LanguageManager {

    /**
     * Adds the language to the manager; necessary for a language to be used at all.
     *
     * @param language The {@link Language} to add
     */
    void addLanguage(Language language);

    /**
     * Initializes the internal language list and checks the requirements for them.
     */
    void initializeLanguages();

    /**
     * Reloads all languages' settings.
     */
    void reloadAllLanguages();

    /**
     * Gets the first language to match the given file extension, if any.
     *
     * @param fileExtension The file extension to match
     * @return The {@link Language} with the given file extension
     */
    Optional<Language> getLanguageFromFileExtension(String fileExtension);

    /**
     * Gets a language by its main class extending {@link Language}.
     *
     * @param clazz The class of the language
     * @return The {@link Language} with the given class, if any
     */
    Optional<Language> getLanguageByClass(Class<?> clazz);

    /**
     * Gets all registered languages despite being enabled or not.
     *
     * @return All {@link Language}s
     */
    List<Language> getAllLanguages();

    /**
     * Gets all enabled languages that the system can use, at least partially.
     *
     * @return All {@link Language} the system can use
     */
    List<Language> getEnabledLanguages();
}

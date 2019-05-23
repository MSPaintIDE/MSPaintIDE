package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.languages.gui.LangGUIOption;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.SettingsAccessor;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LanguageSettings<G> extends SettingsAccessor<G> {

    private String langName;
    private Map<G, Supplier<Object>> defaultGenerators = new HashMap<>();
    private Map<G, LangGUIOption> optionMap = new HashMap<>();

    protected LanguageSettings(String langName) {
        this.langName = langName;
        reload();
    }

    protected abstract String enumToName(G type);

    protected abstract G nameToEnum(String name);

    public void generateDefaults() {
        this.defaultGenerators.forEach((type, generator) -> setSetting(type, generator.get(), false));
    }

    protected void addOption(G type, Object initial, LangGUIOption langGUIOption) {
        addOption(type, initial, langGUIOption, null);
    }

    protected void addOption(G type, Object initial, LangGUIOption langGUIOption, Supplier<Object> defaultGenerator) {
        this.optionMap.put(type, langGUIOption);
        this.defaultGenerators.put(type, defaultGenerator);
        setSetting(type, initial);
        langGUIOption.bindValue(type, this);
        onChangeSetting(type, langGUIOption::setSetting);
    }

    public boolean requiredFilled() {
        return this.optionMap.keySet().stream().allMatch(this::isSet);
    }

    @Override
    public void save() {
        ProjectManager.getPPFProject().setLanguageSetting(this.langName, this.optionMap.keySet()
                .stream()
                .map(langGUIOption -> new AbstractMap.SimpleEntry<>(langGUIOption, this.settings.get(langGUIOption)))
                .collect(Collectors.toMap(g -> g.getKey().toString(), x -> x)));
        ProjectManager.save();
    }

    @Override
    protected void reload() {
        this.settings.clear();
        this.settings = ProjectManager.getPPFProject().getLanguageSetting(langName)
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(nameToEnum(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getLangName() {
        return langName;
    }
}

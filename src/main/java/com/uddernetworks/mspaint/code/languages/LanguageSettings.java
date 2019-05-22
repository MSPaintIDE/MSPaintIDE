package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.SettingsAccessor;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class LanguageSettings<G> extends SettingsAccessor<G> {

    private String langName;

    protected LanguageSettings(String langName) {
        this.langName = langName;
        reload();
    }

    protected abstract G stringToAccessor(String name);

    protected abstract String accessorToString(G accessor);

    private <V> Map<G, V> mapKeyToAccessor(Map<String, V> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> stringToAccessor(e.getKey()), Map.Entry::getValue));
    }

    private <V> Map<String, V> mapKeyToString(Map<G, V> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> accessorToString(e.getKey()), Map.Entry::getValue));
    }

    @Override
    public void save() {
        ProjectManager.getPPFProject().setLanguageSetting(this.langName, mapKeyToString(this.settings));
        ProjectManager.save();
    }

    @Override
    protected void reload() {
        this.settings.clear();
        this.settings = mapKeyToAccessor(ProjectManager.getPPFProject().getLanguageSetting(langName));
    }

    public String getLangName() {
        return langName;
    }
}

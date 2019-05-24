package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.gui.LangGUIOption;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.SettingsAccessor;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LanguageSettings<G> extends SettingsAccessor<G> {

    private String langName;
    private Map<G, Supplier<Object>> defaultGenerators = new HashMap<>();
    private Map<G, LangGUIOption> optionMap = new HashMap<>();

    protected LanguageSettings(String langName) {
        this.langName = langName;
    }

    public abstract void initOptions();

    protected abstract String enumToName(G type);

    protected abstract G nameToEnum(String name);

    public abstract LangGUIOptionRequirement getRequirement(G type);

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

    protected File create(File file) {
        file.mkdirs();
        return file;
    }

    protected File createSubOfProject(String child) {
        var file = new File(ProjectManager.getPPFProject().getFile().getParentFile(), child);
        file.mkdirs();
        return file;
    }

    public boolean requiredFilled() {
        return getOptionsTyped(Predicate.isEqual(LangGUIOptionRequirement.REQUIRED)).stream().allMatch(this::isSet);
    }

    public List<Map.Entry<G, LangGUIOption>> getOptions(Predicate<LangGUIOptionRequirement> predicate) {
        return this.optionMap.entrySet().stream().filter(entry -> predicate.test(getRequirement(entry.getKey()))).collect(Collectors.toList());
    }

    public List<G> getOptionsTyped(Predicate<LangGUIOptionRequirement> predicate) {
        return getOptions(predicate).stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<LangGUIOption> getOptionsGUI(Predicate<LangGUIOptionRequirement> predicate) {
        return getOptions(predicate).stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<LangGUIOption> getOptions() {
        return new ArrayList<>(optionMap.values());
    }

    @Override
    public void save() {
        ProjectManager.getPPFProject().setLanguageSetting(this.langName, this.optionMap.keySet()
                .stream()
                .map(langGUIOption -> new AbstractMap.SimpleEntry<>(langGUIOption, this.settings.get(langGUIOption)))
                .collect(Collectors.toMap(g -> enumToName(g.getKey()), x -> x)));
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

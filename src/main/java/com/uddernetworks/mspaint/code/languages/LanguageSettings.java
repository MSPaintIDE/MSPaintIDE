package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.languages.gui.LangGUIOption;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.SettingsAccessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class LanguageSettings extends SettingsAccessor<Option> {

    private String langName;
    private Map<Option, Supplier<Object>> defaultGenerators = new HashMap<>();
    private Map<Option, LangGUIOption> optionMap = new HashMap<>();

    protected LanguageSettings(String langName) {
        this.langName = langName;
    }

    public abstract void initOptions();

    protected abstract Option nameToEnum(String name);

    public void generateDefaults() {
        this.defaultGenerators.entrySet()
                .stream()
                .filter(entry -> !isSet(entry.getKey()))
                .forEach(entry -> setSetting(entry.getKey(), entry.getValue().get(), false));
    }

    protected void addOption(Option option, LangGUIOption langGUIOption) {
        addOption(option, langGUIOption, null);
    }

    protected void addOption(Option option, LangGUIOption langGUIOption, Supplier<Object> defaultGenerator) {
        this.optionMap.put(option, langGUIOption);
        this.defaultGenerators.put(option, defaultGenerator);
        langGUIOption.bindValue(option, this);
        langGUIOption.setIndex(option.ordinal());
        onChangeSetting(option, langGUIOption::setSetting);
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

    public List<Map.Entry<Option, LangGUIOption>> getOptions(Predicate<LangGUIOptionRequirement> predicate) {
        return this.optionMap.entrySet().stream().filter(entry -> predicate.test(entry.getKey().getRequirement())).collect(Collectors.toList());
    }

    public List<Option> getOptionsTyped(Predicate<LangGUIOptionRequirement> predicate) {
        return getOptions(predicate).stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<LangGUIOption> getOptionsGUI(Predicate<LangGUIOptionRequirement> predicate) {
        return getOptions(predicate).stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<LangGUIOption> getOptions() {
        return new ArrayList<>(optionMap.values());
    }

    @Override
    public boolean optionalRestriction(Option setting) {
        return setting.getRequirement() == LangGUIOptionRequirement.OPTIONAL;
    }

    @Override
    public void save() {
        ProjectManager.getPPFProject().setLanguageSetting(this.langName, this.optionMap.keySet()
                .stream()
                .filter(this::isSet)
                .filter(option -> getSettingOptional(option).isPresent())
                .collect(Collectors.toMap(Option::getName, option -> getSettingOptional(option).get())));
        ProjectManager.save();
    }

    @Override
    protected void reload() {
        this.settings.clear();
        ProjectManager.getPPFProject().getLanguageSetting(langName)
                .forEach((name, value) -> setSetting(nameToEnum(name), value));
    }

    public String getLangName() {
        return langName;
    }
}

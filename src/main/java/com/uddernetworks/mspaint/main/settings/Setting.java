package com.uddernetworks.mspaint.main.settings;

import java.util.Arrays;

import static com.uddernetworks.mspaint.main.settings.SettingType.BOOLEAN;
import static com.uddernetworks.mspaint.main.settings.SettingType.STRING;

public enum Setting {
    OPEN_PROJECT("openProject", STRING),
    DARK_THEME("darkTheme", BOOLEAN);

    private final String name;
    private final SettingType settingType;

    Setting(String name, SettingType settingType) {
        this.name = name;
        this.settingType = settingType;
    }

    public String getName() {
        return name;
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public static Setting fromName(String name) {
        return Arrays.stream(values()).filter(setting -> setting.name.equals(name)).findFirst().orElse(null);
    }
}

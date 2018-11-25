package com.uddernetworks.mspaint.main.settings;

import java.util.Arrays;

import static com.uddernetworks.mspaint.main.settings.SettingType.*;

public enum Setting {
    OPEN_PROJECT("openProject", "", STRING),
    DARK_THEME("darkTheme", true, BOOLEAN),
    DATABASE_URL("databaseURL", "", STRING),
    DATABASE_USER("databaseUser", "", STRING),
    DATABASE_PASS("databasePass", "", STRING),
    TRAIN_IMAGE("trainImage", "", STRING),
    OCR_DEBUG("ocrDebug", false, BOOLEAN),
    EDIT_FILE_SIZE("editFileFontSize", 36, INT); // The font size that files are generated in

    private final String name;
    private Object def;
    private final SettingType settingType;

    Setting(String name, Object def, SettingType settingType) {
        this.name = name;
        this.def = def;
        this.settingType = settingType;
    }

    public String getName() {
        return name;
    }

    public Object getDefault() {
        return this.def;
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public static Setting fromName(String name) {
        return Arrays.stream(values()).filter(setting -> setting.name.equals(name)).findFirst().orElse(null);
    }
}

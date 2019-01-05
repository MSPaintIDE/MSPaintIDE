package com.uddernetworks.mspaint.settings;

public enum SettingType {
    STRING(String.class),
    INT(int.class),
    DOUBLE(double.class),
    BOOLEAN(boolean.class);

    private Class type;

    SettingType(Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }
}

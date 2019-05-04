package com.uddernetworks.mspaint.settings;

import java.util.List;
import java.util.Map;

public enum SettingType {
    STRING(String.class),
    INT(int.class),
    DOUBLE(double.class),
    BOOLEAN(boolean.class),
    STRING_LIST(List.class),
    STRING_STRING_MAP(Map.class);

    private Class type;

    SettingType(Class type) {
        this.type = type;
    }

    public Class getType() {
        return type;
    }
}

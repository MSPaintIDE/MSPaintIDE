package com.uddernetworks.mspaint.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SettingsManager extends SettingsAccessor<Setting> {

    private File file;
    private static SettingsManager instance = new SettingsManager();

    public static SettingsManager getInstance() {
        return instance;
    }

    public void initialize(File file) throws IOException {
        this.file = file;
        reload();
    }

    @Override
    public void save() {
        Properties properties = new Properties();

        settings.forEach((key, value) -> {
            if (value instanceof List) {
                properties.setProperty(key.getName(), String.join(",", (List) value));
            } else if (value instanceof Map) {
                var adding = ((Map<Object, Object>) value).entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + "|" + entry.getValue())
                        .collect(Collectors.joining(","));
                properties.setProperty(key.getName(), adding);
            } else {
                properties.setProperty(key.getName(), value == null ? "" : value.toString());
            }
        });

        try {
            properties.store(new FileOutputStream(file), "MS Paint IDE Global Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() throws IOException {
        settings.clear();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            Properties properties = new Properties();
            Arrays.stream(Setting.values()).forEach(setting -> {
                properties.setProperty(setting.getName(), String.valueOf(setting.getDefault()));
                settings.put(setting, setting.getDefault());
            });

            System.out.println("settings = " + settings);

            properties.store(new FileOutputStream(file), "MS Paint IDE Global Settings");
            return;
        }

        Properties properties = new Properties();
        properties.load(Files.newInputStream(file.toPath()));

        properties.keySet()
                .stream()
                .map(String::valueOf)
                .forEach(key -> {
                    Setting setting = Setting.fromName(key);
                    if (setting == null) return;

                    String string = properties.getProperty(key, "");

                    switch (setting.getSettingType()) {
                        case STRING:
                            settings.put(setting, string);
                            break;
                        case INT:
                            settings.put(setting, string.isEmpty() ? 0 : Integer.valueOf(string));
                            break;
                        case DOUBLE:
                            settings.put(setting, string.isEmpty() ? 0 : Double.valueOf(string));
                            break;
                        case BOOLEAN:
                            settings.put(setting, string.equalsIgnoreCase("true"));
                            break;
                        case STRING_LIST:
                            settings.put(setting, List.of(string.split(",")));
                            break;
                        case STRING_STRING_MAP:
                            if (!string.contains("|")) break;
                            settings.put(setting, Arrays.stream(string.split(",")).map(inner -> {
                                var kv = inner.split("\\|");
                                return new AbstractMap.SimpleEntry<>(kv[0], kv[1]);
                            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                            break;
                    }
                });

        Arrays.stream(Setting.values())
                .filter(setting -> !settings.containsKey(setting))
                .forEach(setting -> settings.put(setting, setting.getDefault()));
    }
}

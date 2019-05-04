package com.uddernetworks.mspaint.settings;

import javafx.application.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SettingsManager {

    private static File file;
    private static Map<Setting, Object> settings = new HashMap<>();
    private static Map<Setting, List<Consumer>> onChangeSettings = new HashMap<>();

    // Everything

    public static Object getSetting(Setting setting) {
        return getSetting(setting, (Object) null);
    }

    public static Object getSetting(Setting setting, Object def) {
        return settings.getOrDefault(setting, def);
    }

    public static <T> T getSetting(Setting setting, Class<T> type) {
        return getSetting(setting, type, null);
    }

    public static <T> T getSetting(Setting setting, Class<T> type, T def) {
        return type.cast(settings.getOrDefault(setting, def));
    }


    // Lists
    public static List getSettingList(Setting setting) {
        return (List) settings.get(setting);
    }

    public static <T> List<T> getSettingList(Setting setting, Class<T> type) {
        return (List<T>) settings.get(setting);
    }

    public static <T> List<T> getSettingList(Setting setting, T def) {
        return (List<T>) settings.getOrDefault(setting, def);
    }



    // Maps
    public static Map getSettingMap(Setting setting) {
        return (Map) settings.get(setting);
    }

    public static <T> Map<T, T> getSettingMap(Setting setting, Class<T> kvType) {
        return (Map<T, T>) settings.get(setting);
    }

    public static <K, V> Map<K, V> getSettingMap(Setting setting, Class<K> keyType, Class<V> valueType) {
        return (Map<K, V>) settings.get(setting);
    }

    public static <K, V> Map<K, V> getSettingMap(Setting setting, Class<K> keyType, Class<V> valueType, V def) {
        return (Map<K, V>) settings.getOrDefault(setting, def);
    }



    public static void setSetting(Setting setting, Object value) {
        settings.put(setting, value);
        Platform.runLater(() -> onChangeSettings.getOrDefault(setting, Collections.emptyList()).forEach(consumer -> consumer.accept(value)));
        save();
    }

    public static <T> void onChangeSetting(Setting setting, Consumer<T> consumer, Class<T> clazz) {
        onChangeSetting(setting, consumer, clazz, false);
    }

    public static <T> void onChangeSetting(Setting setting, Consumer<T> consumer, Class<T> clazz, boolean runInitial) {
        onChangeSettings.putIfAbsent(setting, new ArrayList<>());
        onChangeSettings.get(setting).add(consumer);
        if (runInitial) consumer.accept((T) settings.get(setting));
    }

    public static void initialize(File file) throws IOException {
        SettingsManager.file = file;
        reload();
    }

    public static void save() {
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

    public static void reload() throws IOException {
        settings.clear();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            Properties properties = new Properties();
            Arrays.stream(Setting.values()).forEach(setting -> {
                properties.setProperty(setting.getName(), String.valueOf(setting.getDefault()));
                settings.put(setting, setting.getDefault());
            });

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

package com.uddernetworks.mspaint.main.settings;

import javafx.application.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class SettingsManager {

    private static File file;
    private static Map<Setting, Object> settings = new HashMap<>();
    private static Map<Setting, List<Consumer>> onChangeSettings = new HashMap<>();

    public static Object getSetting(Setting setting) {
        return settings.getOrDefault(setting, null);
    }

    public static <T> T getSetting(Setting setting, Class<T> type) {
        return type.cast(settings.getOrDefault(setting, null));
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
        file.createNewFile();
        SettingsManager.file = file;
        reload();
    }

    public static void save() {
        Properties properties = new Properties();

        settings.forEach((key, value) -> properties.setProperty(key.getName(), value.toString()));

        try {
            properties.store(new FileOutputStream(file), "MS Paint IDE Global Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() throws IOException {
        settings.clear();

        Properties properties = new Properties();
        properties.load(Files.newInputStream(file.toPath()));

        properties.keySet()
                .stream()
                .map(String::valueOf)
                .forEach(key -> {
                    Setting setting = Setting.fromName(key);
                    if (setting == null) return;

                    String string = properties.getProperty(key, null);

                    switch (setting.getSettingType()) {
                        case STRING:
                            settings.put(setting, string);
                            break;
                        case INT:
                            settings.put(setting, Integer.valueOf(string));
                            break;
                        case DOUBLE:
                            settings.put(setting, Double.valueOf(string));
                            break;
                        case BOOLEAN:
                            settings.put(setting, string.equalsIgnoreCase("true"));
                            break;
                    }
                });
    }

}

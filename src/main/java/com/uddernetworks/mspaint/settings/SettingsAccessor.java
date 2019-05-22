package com.uddernetworks.mspaint.settings;

import javafx.application.Platform;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public abstract class SettingsAccessor<G> {

    protected Map<G, Object> settings = new HashMap<>();
    protected Map<G, List<Consumer>> onChangeSettings = new HashMap<>();

    // Everything
    public <T> T getSetting(G setting) {
        return getSetting(setting, null);
    }

    public <T> T getSetting(G setting, T def) {
        return (T) settings.getOrDefault(setting, def);
    }

    // Lists
    public <T> List<T> getSettingList(G setting) {
        return getSettingList(setting, null);
    }

    public <T> List<T> getSettingList(G setting, T def) {
        return (List<T>) settings.getOrDefault(setting, def);
    }

    // Maps
    public <K, V> Map<K, V> getSettingMap(G setting) {
        return getSettingMap(setting, null);
    }

    public <K, V> Map<K, V> getSettingMap(G setting, V def) {
        return (Map<K, V>) settings.getOrDefault(setting, def);
    }

    public void setSetting(G setting, Object value) {
        settings.put(setting, value);
        Platform.runLater(() -> onChangeSettings.getOrDefault(setting, Collections.emptyList()).forEach(consumer -> consumer.accept(value)));
        save();
    }

    public <T> void onChangeSetting(G setting, Consumer<T> consumer) {
        onChangeSetting(setting, consumer, false);
    }

    public <T> void onChangeSetting(G setting, Consumer<T> consumer, boolean runInitial) {
        onChangeSettings.putIfAbsent(setting, new ArrayList<>());
        onChangeSettings.get(setting).add(consumer);
        if (runInitial) consumer.accept((T) settings.get(setting));
    }

    protected abstract void save();

    protected abstract void reload() throws IOException;

}

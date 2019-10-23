package com.uddernetworks.mspaint.settings;

import com.uddernetworks.mspaint.gui.window.search.ReplaceManager;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class SettingsAccessor<G> {

    public Map<G, Object> settings = new HashMap<>();
    protected Map<G, List<Consumer>> onChangeSettings = new HashMap<>();

    public boolean isSet(G setting) {
        return this.settings.containsKey(setting);
    }

    // Everything
    public <T> Optional<T> getSettingOptional(G setting) {
        return Optional.ofNullable((T) settings.get(setting));
    }

    public <T> T getSetting(G setting) {
        return getSetting(setting, null);
    }

    public <T> T getSetting(G setting, T def) {
        checkSetting(setting);
        return (T) settings.getOrDefault(setting, def);
    }

    // Lists
    public <T> Optional<List<T>> getSettingListOptional(G setting) {
        return Optional.ofNullable((List<T>) settings.get(setting));
    }

    public <T> List<T> getSettingList(G setting) {
        return getSettingList(setting, null);
    }

    public <T> List<T> getSettingList(G setting, T def) {
        checkSetting(setting);
        return (List<T>) settings.getOrDefault(setting, def);
    }

    // Maps
    public <K, V> Optional<Map<K, V>> getSettingMapOptional(G setting) {
        return Optional.ofNullable((Map<K, V>) settings.get(setting));
    }

    public <K, V> Map<K, V> getSettingMap(G setting) {
        return getSettingMap(setting, null);
    }

    public <K, V> Map<K, V> getSettingMap(G setting, V def) {
        checkSetting(setting);
        return (Map<K, V>) settings.getOrDefault(setting, def);
    }

    public void removeSetting(G setting) {
        this.settings.remove(setting);
    }

    private static Logger LOGGER = LoggerFactory.getLogger(ReplaceManager.class);

    public void setSetting(G setting, Object value) {
        setSetting(setting, value, true);
    }

    public void setSetting(G setting, Object value, boolean override) {
        setSetting(setting, value, override, true);
    }

    public void setSetting(G setting, Object value, boolean override, boolean runOnChange) {
        if (override || this.settings.get(setting) == null) {
            settings.put(setting, value);
            if (runOnChange) {
                Platform.runLater(() -> this.onChangeSettings.getOrDefault(setting, Collections.emptyList()).forEach(consumer -> consumer.accept(value)));
            }

            save();
        }
    }

    public <T> void onChangeSetting(G setting, Consumer<T> consumer) {
        onChangeSetting(setting, consumer, false);
    }

    public <T> void onChangeSetting(G setting, Consumer<T> consumer, boolean runInitial) {
        onChangeSettings.putIfAbsent(setting, new ArrayList<>());
        onChangeSettings.get(setting).add(consumer);
        if (runInitial) consumer.accept((T) settings.get(setting));
    }

    private void checkSetting(G setting) {
        if (optionalRestriction(setting)) throw new RequiresOptionalGetter(setting.toString());
    }

    /**
     * Gets if the given setting is required to be fetched via {@link SettingsAccessor#getSettingOptional(Object)} and
     * other optional methods. This will cause the method used to throw a runtime exception.
     *
     * @param setting The setting to test
     * @return False if the setting san be used by any method, true if there is no restriction present.
     */
    public boolean optionalRestriction(G setting) {
        return false;
    }

    public abstract void save();

    public abstract void reload() throws IOException;

}

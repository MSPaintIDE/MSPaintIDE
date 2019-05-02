package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ThemeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(ThemeManager.class);

    private Map<String, String> themes = new HashMap<>();
    private List<Scene> scenes = new ArrayList<>();
    private String current;

    public void loadTheme(String name, String path) {
        LOGGER.info("Loading theme \"" + name + "\"");
        themes.put(name, "themes/" + path);
    }

    public void init() {
        SettingsManager.onChangeSetting(Setting.EXTRA_THEME, theme -> selectTheme(this.current = theme), String.class, true);
    }

    public List<String> getAllThemes() {
        return new ArrayList<>(this.themes.keySet());
    }

    public void addStage(Stage stage) {
        Scene scene = stage.getScene();
        stage.setOnCloseRequest(event -> removeScene(scene));
        if (!this.scenes.contains(scene)) this.scenes.add(scene);
        selectTheme(this.current);
    }

    public void removeScene(Scene scene) {
        this.scenes.remove(scene);
    }

    public void selectTheme(String name) {
        if (!this.themes.containsKey(name)) return;
        this.scenes.stream().map(Scene::getStylesheets).forEach(sheets -> {
            if (this.current != null) sheets.remove(this.themes.get(current));
            sheets.add(this.themes.get(name));
        });
    }

    public ThemeChanger onDarkThemeChange(Parent root, Map<String, String> classToDark) {
        var initialMap = new HashMap<>(Map.of(
                ".gridpane-theme", "gridpane-theme-dark",
                ".theme-text", "dark-text"));
        initialMap.putAll(classToDark);
        return new ThemeChanger(root, initialMap);
    }

    public class ThemeChanger {
        private Parent root;
        private Map<String, String> classToDark;
        private Consumer<Boolean> onChange = newValue ->
                classToDark.forEach((key, value) -> root.lookupAll(key)
                        .stream()
                        .map(Node::getStyleClass)
                        .forEach(styles -> {
                            if (newValue) {
                                styles.add(value);
                            } else {
                                styles.remove(value);
                            }
                        }));

        public ThemeChanger(Parent root, Map<String, String> classToDark) {
            this.root = root;
            this.classToDark = classToDark;
            init();
        }

        private void init() {
            SettingsManager.onChangeSetting(Setting.DARK_THEME, onChange, boolean.class, true);
        }

        public void update() {
            onChange.accept(SettingsManager.getSetting(Setting.DARK_THEME, Boolean.class));
        }
    }
}

package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeManager {

    private Map<String, String> themes = new HashMap<>();
    private List<Scene> scenes = new ArrayList<>();
    private String current;

    public ThemeManager() {
        SettingsManager.onChangeSetting(Setting.EXTRA_THEME, this::selectTheme, String.class, false);
    }

    public void loadTheme(String name, String path) {
        System.out.println("Loading theme \"" + name + "\"");
        themes.put(name, "themes/" + path);
    }

    public List<String> getAllThemes() {
        return new ArrayList<>(this.themes.keySet());
    }

    public void addStage(Stage stage) {
        Scene scene = stage.getScene();
        stage.setOnCloseRequest(event -> removeScene(scene));
        if (!this.scenes.contains(scene)) this.scenes.add(scene);
    }

    public void removeScene(Scene scene) {
        this.scenes.remove(scene);
    }

    public void selectTheme(String name) {
        if (!this.themes.containsKey(name)) return;
        this.scenes.stream().map(Scene::getStylesheets).forEach(sheets -> {
            if (this.current != null) sheets.remove(current);
            sheets.add(this.current = this.themes.get(name));
        });
    }
}

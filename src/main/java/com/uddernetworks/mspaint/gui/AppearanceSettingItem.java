package com.uddernetworks.mspaint.gui;

import com.uddernetworks.mspaint.gui.elements.SettingKV;
import com.uddernetworks.mspaint.gui.kvselection.KVData;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AppearanceSettingItem extends Stage implements SettingItem, Initializable {

    @FXML
    private SettingKV themeSetting;

    private String name;
    private String file;
    private MainGUI mainGUI;
    private ThemeManager.ThemeChanger themeChanger;

    public AppearanceSettingItem(String name, String file, MainGUI mainGUI) {
        this.name = name;
        this.file = file;
        this.mainGUI = mainGUI;
    }

    @Override
    public Pane getPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.file));
        loader.setController(this);
        Parent root = loader.load();
        Node node = root.lookup("*");

        if (!(node instanceof Pane)) throw new LoadException("Root element of " + this.file + " not a pane!");

        this.themeChanger = this.mainGUI.getThemeManager().onDarkThemeChange((Pane) node, Map.of(
                "#fontSelect", "dark",
                ".remove-entry", "remove-entry-white"
        ));

        this.themeChanger.update(100, TimeUnit.MILLISECONDS);

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var themeManager = this.mainGUI.getThemeManager();

        this.themeSetting.generateInital(() -> {
            var activeName = themeManager.getActiveTheme();
            return themeManager.getAllThemes().entrySet()
                    .stream().map(entry -> new KVData(entry.getKey(), entry.getValue(), activeName.equals(entry.getKey())))
                    .collect(Collectors.toList());
        });

        this.themeSetting.initLogic(this.mainGUI);

        this.themeSetting.onKeyChange((cell, oldValue, newValue) -> themeManager.modifyThemeName(cell.getItem().getName(), newValue));

        this.themeSetting.onValueChange((cell, oldValue, newValue) -> themeManager.modifyThemePath(cell.getItem().getName(), newValue));

        this.themeSetting.generateDefault(first -> {
            if (first.stream().anyMatch(data -> data.getName() == null)) return Optional.empty();
            return Optional.of(new KVData(null, null, first.isEmpty()));
        });

        this.themeSetting.onKVRemove(cell -> themeManager.removeThemeName(cell.getItem().getName()));

        this.themeSetting.onKVActive(kvData -> {
            if (!kvData.getName().equals(themeManager.getActiveTheme())) {
                SettingsManager.getInstance().setSetting(Setting.ACTIVE_THEME_NAME, kvData.getName());
                themeManager.selectThemeName(kvData.getPath());
            }
        });
    }

    @Override
    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return name;
    }
}

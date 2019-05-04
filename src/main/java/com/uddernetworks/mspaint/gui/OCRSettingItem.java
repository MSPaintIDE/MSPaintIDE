package com.uddernetworks.mspaint.gui;

import com.uddernetworks.mspaint.gui.elements.SettingKV;
import com.uddernetworks.mspaint.gui.kvselection.KVData;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OCRSettingItem extends Stage implements SettingItem, Initializable {

    @FXML
    private SettingKV fontKV;

    private String name;
    private String file;
    private MainGUI mainGUI;
    private ThemeManager.ThemeChanger themeChanger;

    public OCRSettingItem(String name, String file, MainGUI mainGUI) {
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
        this.fontKV.generateInital(() -> {
            var currProject = ProjectManager.getPPFProject();
            return currProject.getFonts().entrySet()
                    .stream()
                    .map((entry) -> new KVData(entry.getKey(), entry.getValue(), currProject.getActiveFont().equals(name)))
                    .collect(Collectors.toList());
        });

        this.fontKV.initLogic(this.mainGUI);

        this.fontKV.onKeyChange((cell, oldValue, newValue) -> getAndSaveProject(project -> {
            var name = cell.getItem().getName();
            if (project.getActiveFont() == null) {
                project.setActiveFont(name);
            }

            project.modifyFontName(name, newValue);
        }));

        this.fontKV.onValueChange((cell, oldValue, newValue) -> getAndSaveProject(project -> project.modifyFontPath(cell.getItem().getName(), newValue)));

        this.fontKV.generateDefault(first -> {
            if (first.stream().anyMatch(data -> data.getName() == null)) return Optional.empty();
            return Optional.of(new KVData(null, null, first.isEmpty()));
        });

        this.fontKV.onKVRemove(cell -> getAndSaveProject(project -> project.removeFont(cell.getItem().getName())));

        this.fontKV.onKVActive(kvData -> getAndSaveProject(project -> {
            if (!kvData.getName().equals(project.getActiveFont())) project.setActiveFont(kvData.getName());
        }));
    }

    private void getAndSaveProject(Consumer<PPFProject> projectConsumer) {
        var currProject = ProjectManager.getPPFProject();
        projectConsumer.accept(currProject);
        ProjectManager.save();
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

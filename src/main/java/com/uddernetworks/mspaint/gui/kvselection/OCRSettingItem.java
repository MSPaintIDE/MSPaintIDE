package com.uddernetworks.mspaint.gui.kvselection;

import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.gui.SettingItem;
import com.uddernetworks.mspaint.gui.elements.SettingKV;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class OCRSettingItem extends Stage implements SettingItem, Initializable {

    @FXML
    private JFXListView<KVData> fontSelect;

    @FXML
    private Hyperlink addFontText;

    @FXML
    private SettingKV fontKV;

    private String name;
    private String file;
    private MainGUI mainGUI;
    private ThemeManager.ThemeChanger themeChanger;
    private Method addURL;
    private List<String> added = new ArrayList<>();

    public OCRSettingItem(String name, String file, MainGUI mainGUI) {
        this.name = name;
        this.file = file;
        this.mainGUI = mainGUI;
    }

    @Override
    public Pane getPane() throws IOException {
        addPath(MainGUI.APP_DATA.getAbsolutePath());

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.file));
        loader.setController(this);
        Parent root = loader.load();
        Node node = root.lookup("*");

        if (!(node instanceof Pane)) throw new LoadException("Root element of " + this.file + " not a pane!");

        this.themeChanger = this.mainGUI.getThemeManager().onDarkThemeChange((Pane) node, Map.of(
                "#fontSelect", "dark",
                ".remove-entry", "remove-entry-white",
                ".jfx-list-view", "gridpane-theme-dark"
        ));

        this.themeChanger.update(100, TimeUnit.MILLISECONDS);

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fontKV.initLogic(this.mainGUI, () -> this.themeChanger);

        fontKV.onKeyChange((cell, oldValue, newValue) -> {
            getAndSaveProject(project -> {
                var name = cell.getItem().getName();
                if (project.getActiveFont() == null) {
                    project.setActiveFont(name);
                }

                project.modifyFontName(name, newValue);
            });
        });

        fontKV.onValueChange((cell, oldValue, newValue) -> {
            getAndSaveProject(project -> {
                project.modifyFontPath(cell.getItem().getName(), newValue);
            });
        });

        fontKV.generateDefault(first -> {
            if (first.stream().anyMatch(data -> data.getName() == null)) return Optional.empty();
            return Optional.of(new KVData(null, null, first.isEmpty()));
        });

        fontKV.onKVRemove(cell -> getAndSaveProject(project -> project.removeFont(cell.getItem().getName())));

        fontKV.onKVActive(kvData -> getAndSaveProject(project -> {
            if (!kvData.getName().equals(project.getActiveFont())) project.setActiveFont(kvData.getName());
        }));
    }

    private void getAndSaveProject(Consumer<PPFProject> projectConsumer) {
        var currProject = ProjectManager.getPPFProject();
        projectConsumer.accept(currProject);
        ProjectManager.save();
    }

    public void addPath(String path) {
        try {
            if (this.added.contains(path)) return;
            if (this.addURL == null) {
                this.addURL = ClassLoader.getSystemClassLoader().getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                this.addURL.setAccessible(true);
            }

            this.addURL.invoke(ClassLoader.getSystemClassLoader(), path);
            this.added.add(path);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
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

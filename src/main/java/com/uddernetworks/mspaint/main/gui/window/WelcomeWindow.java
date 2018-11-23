package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class WelcomeWindow extends Stage implements Initializable {

    @FXML
    private JFXButton createProject;

    @FXML
    private JFXButton importProject;

    @FXML
    private JFXButton openProject;

    @FXML
    private JFXListView<PPFProject> recentProjects;

    private MainGUI mainGUI;
    private Runnable ready;

    public WelcomeWindow(MainGUI mainGUI, Runnable ready) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.ready = ready;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/ProjectManageWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Welcome to MS Paint IDE");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        show();

        setTitle("Welcome to MS Paint IDE");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put("gridpane-theme", "gridpane-theme-dark");
        changeDark.put("logo-image", "dark");
        changeDark.put("recent-projects", "recent-projects-dark");

        SettingsManager.onChangeSetting(Setting.DARK_THEME, newValue ->
                changeDark.forEach((key, value) -> root.lookupAll("." + key)
                        .stream()
                        .map(Node::getStyleClass)
                        .forEach(styles -> {
                            if (newValue) {
                                styles.add(value);
                            } else {
                                styles.remove(value);
                            }
                        })), boolean.class, true);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentProjects.setItems(FXCollections.observableList(ProjectManager.getRecent()));

        recentProjects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ProjectManager.switchProject(newValue);
            this.ready.run();
            Platform.runLater(this::close);
        });

        createProject.setOnAction(event -> {
            try {
                new CreateProjectWindow(this.mainGUI, this.ready);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        importProject.setOnAction(event -> {
            // TODO: Not sure how to import projects/what to import from
        });

        openProject.setOnAction(event -> {
            File openAt = ProjectManager.getPPFProject() != null ? ProjectManager.getPPFProject().getFile() : new File("");
            FileDirectoryChooser.openFileChooser(openAt, new FileNameExtensionFilter("Paint Project File", "ppf"), JFileChooser.FILES_ONLY, file -> {
                ProjectManager.switchProject(ProjectManager.readProject(file));
                this.ready.run();
                Platform.runLater(this::close);
            });
        });
    }
}

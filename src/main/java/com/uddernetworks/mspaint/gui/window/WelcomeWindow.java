package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    public WelcomeWindow(MainGUI mainGUI) throws IOException {
        this.mainGUI = mainGUI;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/ProjectManageWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Welcome to MS Paint IDE");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        //this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Welcome to MS Paint IDE");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        System.out.println("mainGUI = " + mainGUI);
        System.out.println("mainGUI.getThemeManager() = " + mainGUI.getThemeManager());
        this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of(
                ".logo-image", "dark",
                ".recent-projects", "recent-projects-dark"
        ));
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentProjects.setItems(FXCollections.observableList(ProjectManager.getRecent()));

        recentProjects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ProjectManager.switchProject(newValue);
            this.mainGUI.refreshProject();
            Platform.runLater(this::close);
        });

        createProject.setOnAction(event -> {
            try {
                new CreateProjectWindow(this.mainGUI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        importProject.setOnAction(event -> {
            // TODO: Not sure how to import projects/what to import from
        });

        openProject.setOnAction(event -> {
            File openAt = ProjectManager.getPPFProject() != null ? ProjectManager.getPPFProject().getFile() : new File("");
            FileDirectoryChooser.openFileChooser(openAt, ProjectFileFilter.PPF, JFileChooser.FILES_ONLY, file -> {
                ProjectManager.switchProject(ProjectManager.readProject(file));
                this.mainGUI.refreshProject();
                Platform.runLater(this::close);
            });
        });
    }
}

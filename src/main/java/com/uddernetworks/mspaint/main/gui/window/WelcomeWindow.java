package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ProjectManageWindow.fxml"));
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
        getIcons().add(icon.getImage());
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentProjects.setItems(FXCollections.observableList(ProjectManager.getRecent()));

        createProject.setOnAction(event -> {
            try {
                new CreateProjectWindow(this.mainGUI, ready);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        importProject.setOnAction(event -> {

        });

        openProject.setOnAction(event -> {

        });
    }
}

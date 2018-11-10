package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.languages.Language;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateProjectWindow extends Stage implements Initializable {

    @FXML
    private JFXTextField projectName;

    @FXML
    private JFXTextField projectLocation;

    @FXML
    private JFXButton browse;

    @FXML
    private JFXComboBox<Language> languageComboBox;

    @FXML
    private JFXButton finish;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton help;

    @FXML
    private Label error;

    private MainGUI mainGUI;
    private Runnable ready;

    public CreateProjectWindow(MainGUI mainGUI, Runnable ready) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.ready = ready;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("CreateProject.fxml"));
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
        File startAt = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");
        this.languageComboBox.setItems(mainGUI.getLanguages());

        finish.setOnAction(event -> {
            try {
                File fileLocation = new File(projectLocation.getText());
                String name = projectName.getText();
                Language language = languageComboBox.getValue();
                if (!fileLocation.exists() && !fileLocation.createNewFile()) {
                    error.setText("Couldn't find or create project directory!");
                    return;
                }

                PPFProject ppfProject = new PPFProject(new File(fileLocation, name.replaceAll("[^\\w\\-. ]+", "") + ".ppf"));
                ppfProject.setName(name);
                ppfProject.setLanguage(language.getName());

                Platform.runLater(() -> {
                    ProjectManager.setCurrentProject(ppfProject);
                    ProjectManager.save();
                    ProjectManager.addRecent(ppfProject);
                    ProjectManager.writeRecent();
                    ready.run();
                    close();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        browse.setOnAction(event -> {
            FileDirectoryChooser.openFileChooser(startAt, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                projectLocation.setText(file.getAbsolutePath());
            });
        });

        cancel.setOnAction(event -> close());

        help.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/RubbaBoy/MSPaintIDE/blob/master/README.md"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
}

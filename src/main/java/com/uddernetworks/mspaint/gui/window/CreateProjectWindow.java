package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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

    public CreateProjectWindow(MainGUI mainGUI) throws IOException {
        super();
        this.mainGUI = mainGUI;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/CreateProject.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo-small.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Welcome to MS Paint IDE");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Welcome to MS Paint IDE");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put("gridpane-theme", "gridpane-theme-dark");
        changeDark.put("theme-text", "dark-text");
        changeDark.put("search-label", "dark");
        changeDark.put("found-context", "dark");
        changeDark.put("language-selection", "language-selection-dark");

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
        File startAt = MainGUI.LOCAL_MSPAINT;
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
                ppfProject.setLanguage(language.getClass().getCanonicalName());

                Platform.runLater(() -> {
                    ProjectManager.switchProject(ppfProject);
                    this.mainGUI.refreshProject();
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

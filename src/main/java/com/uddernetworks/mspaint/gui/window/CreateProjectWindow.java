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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    private boolean isStatic;
    private File staticFile;

    public CreateProjectWindow(MainGUI mainGUI) throws IOException {
        super();
        this.mainGUI = mainGUI;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/CreateProject.fxml"));
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
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Welcome to MS Paint IDE");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of(".search-label", "dark",
                ".found-context", "dark",
                ".language-selection", "language-selection-dark"
        ));
    }

    private void updateLocation() {
        if (!this.isStatic) return;
        this.projectLocation.setText(this.staticFile.getAbsolutePath() + "\\" + projectName.getText());
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File startAt = MainGUI.APP_DATA;
        this.languageComboBox.setItems(mainGUI.getLanguages());

        this.languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            var staticParentOptional = newValue.getStaticParent();
            this.isStatic = staticParentOptional.isPresent();
            this.projectLocation.setEditable(!isStatic);
            this.projectLocation.setDisable(isStatic);
            if (isStatic) this.staticFile = staticParentOptional.get();
            updateLocation();
        });

        this.projectName.textProperty().addListener((observable, oldValue, newValue) -> updateLocation());

        finish.setOnAction(event -> {
            File fileLocation = new File(projectLocation.getText());
            String name = projectName.getText();
            Language language = languageComboBox.getValue();
            if (!fileLocation.exists() && !fileLocation.mkdirs()) {
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
        });

        browse.setOnAction(event -> {
            FileDirectoryChooser.openDirectorySelector(chooser ->
                    chooser.setInitialDirectory(startAt), file ->
                    projectLocation.setText(file.getAbsolutePath()));
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

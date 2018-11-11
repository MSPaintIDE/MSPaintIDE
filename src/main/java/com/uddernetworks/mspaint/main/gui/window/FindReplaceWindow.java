package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.window.search.SearchResult;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FindReplaceWindow extends Stage implements Initializable {

    @FXML
    private JFXTextField searchText;

    @FXML
    private JFXTextField replaceText;

    @FXML
    private JFXTextField directoryPath;

    @FXML
    private JFXButton search;

    @FXML
    private JFXButton replace;

    @FXML
    private JFXButton browse;

    @FXML
    private JFXButton openFile;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXListView<SearchResult> searchResults;

    @FXML
    private JFXRadioButton inFile;

    @FXML
    private JFXRadioButton inProject;

    @FXML
    private JFXRadioButton inDirectory;

    @FXML
    private JFXCheckBox replaceToggle;


    private MainGUI mainGUI;
    private boolean initiallyReplace;

    public FindReplaceWindow(MainGUI mainGUI, boolean replace) throws IOException {
        super();
        this.mainGUI = mainGUI;
        initiallyReplace = replace;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/FindReplace.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Find" + (replace ? "/Replace" : ""));

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        show();

        setTitle("Find" + (replace ? "/Replace" : ""));
        getIcons().add(icon.getImage());

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put("gridpane-theme", "gridpane-theme-dark");
        changeDark.put("theme-text", "dark-text");

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
        inFile.setSelected(true);
        search.setOnAction(event -> {
            System.out.println("Search settings:");
            System.out.println("inFile = " + inFile.isSelected());
            System.out.println("inProject = " + inProject.isSelected());
            System.out.println("inDirectory = " + inDirectory.isSelected());
        });

        replaceToggle.setSelected(initiallyReplace);
        if (!initiallyReplace) {
            replaceText.setDisable(true);
            replace.setDisable(true);
        }

        replaceToggle.selectedProperty().addListener((observer, oldValue, newValue) -> {
            replaceText.setDisable(!newValue);
            replace.setDisable(!newValue);
        });

        replace.setOnAction(event -> {

        });

        browse.setOnAction(event -> {
            if (inProject.isSelected()) return;
            boolean selectingFile = inFile.isSelected();

            FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, selectingFile ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY, file -> {
                directoryPath.setText(file.getAbsolutePath());
            });
        });

        openFile.setOnAction(event -> {

        });

        cancel.setOnAction(event -> close());
    }
}

package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.window.search.SearchListCell;
import com.uddernetworks.mspaint.main.gui.window.search.SearchManager;
import com.uddernetworks.mspaint.main.gui.window.search.SearchResult;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FindReplaceWindow extends Stage implements Initializable {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

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

    @FXML
    private JFXCheckBox caseInsensitive;

    @FXML
    private JFXTextField fileExtension;

    @FXML
    private Label notice;


    private MainGUI mainGUI;
    private SearchManager searchManager;
    private boolean initiallyReplace;

    public FindReplaceWindow(MainGUI mainGUI, boolean replace) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.searchManager = new SearchManager(mainGUI);
        this.initiallyReplace = replace;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/FindReplace.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        setResizable(false);

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, false, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Find" + (replace ? "/Replace" : ""));

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        show();

        setTitle("Find" + (replace ? "/Replace" : ""));
        getIcons().add(icon.getImage());

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put(".gridpane-theme", "gridpane-theme-dark");
        changeDark.put(".theme-text", "dark-text");
        changeDark.put("#searchResults", "dark");

        SettingsManager.onChangeSetting(Setting.DARK_THEME, newValue ->
                changeDark.forEach((key, value) -> root.lookupAll(key)
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

    private void setError(String error) {
        Platform.runLater(() -> {
            this.notice.setTextFill(Color.RED);
            this.notice.setText(error);
        });
    }

    private void setNotice(String notice) {
        Platform.runLater(() -> {
            this.notice.setTextFill(Color.GRAY);
            this.notice.setText(notice);
        });
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchResults.setCellFactory(t -> new SearchListCell());

        inFile.setSelected(true);
        search.setOnAction(event -> {
            executorService.execute(() -> {
                long start = System.currentTimeMillis();
                setError(null);
                String extension = fileExtension.getText();
                if (extension.startsWith(".")) extension = extension.substring(1);

                if (extension.isEmpty() || extension.matches("[^a-zA-Z\\d_.]")) {
                    setError("You have an invalid file extension!");
                    return;
                }

                List<SearchResult> results;

                if (inFile.isSelected()) {
                    results = this.searchManager.searchFile(new File(directoryPath.getText()), searchText.getText(), caseInsensitive.isSelected());
                } else if (inProject.isSelected()) {
                    results = this.searchManager.searchProject(searchText.getText(), extension, caseInsensitive.isSelected());
                } else {
                    results = this.searchManager.searchDirectory(new File(directoryPath.getText()), searchText.getText(), extension, caseInsensitive.isSelected());
                }

                Platform.runLater(() -> {
                    ObservableList<SearchResult> items = searchResults.getItems();
                    items.clear();
                    items.addAll(results);

                    setNotice("Found " + results.size() + " results in " + (System.currentTimeMillis() - start) + "ms");
                });
            });
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

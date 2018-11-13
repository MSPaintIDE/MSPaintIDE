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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class FindReplaceWindow extends Stage implements Initializable {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @FXML
    private JFXTextField searchText;

    @FXML
    private JFXTextField replaceText;

    @FXML
    private JFXTextField directoryPath;

    @FXML
    private JFXButton action;

    @FXML
    private JFXButton browse;

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

    private List<SearchResult> searchFor(String extension, boolean inFile, boolean inProject, String directoryPath, String searchText, boolean caseInsensitive) {
        if (extension.startsWith(".")) extension = extension.substring(1);

        if (extension.isEmpty() || extension.matches("[^a-zA-Z\\d_.]")) {
            setError("You have an invalid file extension!");
            return Collections.emptyList();
        }

        if (inFile) {
            return this.searchManager.searchFile(new File(directoryPath), searchText, caseInsensitive);
        } else if (inProject) {
            return this.searchManager.searchProject(searchText, extension, caseInsensitive);
        }

        return this.searchManager.searchDirectory(new File(directoryPath), searchText, extension, caseInsensitive);
    }

    private final AtomicLong lastChanged = new AtomicLong();
    private final AtomicBoolean alreadySearched = new AtomicBoolean(false);
    private final AtomicBoolean hasChanged = new AtomicBoolean(false);

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchResults.setCellFactory(t -> new SearchListCell());

        new Thread(() -> {
            try {
                while (true) {
                    if (hasChanged.get()) {
                        lastChanged.set(System.currentTimeMillis());
                        alreadySearched.set(false);
                        hasChanged.set(false);
                    } else if (!alreadySearched.get() && lastChanged.get() + 250 <= System.currentTimeMillis()) {
                        long start = System.currentTimeMillis();
                        setError(null);

                        Platform.runLater(() -> {
                            final String fileExtension = this.fileExtension.getText();
                            final boolean inFile = this.inFile.isSelected();
                            final boolean inProject = this.inProject.isSelected();
                            final String directoryPath = this.directoryPath.getText();
                            final String searchText = this.searchText.getText();
                            final boolean caseInsensitive = this.caseInsensitive.isSelected();

                            if (searchText.trim().isEmpty()) {
                                alreadySearched.set(true);
                                this.searchResults.getItems().clear();
                                return;
                            }

                            executorService.execute(() -> {
                                List<SearchResult> results = searchFor(fileExtension, inFile, inProject, directoryPath, searchText, caseInsensitive);

                                Platform.runLater(() -> {
                                    ObservableList<SearchResult> items = this.searchResults.getItems();
                                    items.clear();
                                    items.addAll(results);

                                    setNotice("Found " + results.size() + " results in " + (System.currentTimeMillis() - start) + "ms");

                                    alreadySearched.set(true);
                                });
                            });
                        });
                    }

                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
        }).start();

        this.searchText.textProperty().addListener(((observable, oldValue, newValue) -> hasChanged.set(true)));

        inFile.setSelected(true);
        action.setOnAction(event -> {

        });

        replaceToggle.setSelected(initiallyReplace);
        if (!initiallyReplace) {
            replaceText.setDisable(true);
            action.setText("Search");
        }

        replaceToggle.selectedProperty().addListener((observer, oldValue, newValue) -> {
            replaceText.setDisable(!newValue);
            action.setText(newValue ? "Replace" : "Search");
        });

        browse.setOnAction(event -> {
            if (inProject.isSelected()) return;
            boolean selectingFile = inFile.isSelected();

            FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, selectingFile ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY, file -> {
                directoryPath.setText(file.getAbsolutePath());
            });
        });

        cancel.setOnAction(event -> close());
    }
}

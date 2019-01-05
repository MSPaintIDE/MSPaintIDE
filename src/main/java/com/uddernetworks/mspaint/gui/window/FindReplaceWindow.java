package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.gui.window.search.ReplaceManager;
import com.uddernetworks.mspaint.gui.window.search.SearchListCell;
import com.uddernetworks.mspaint.gui.window.search.SearchManager;
import com.uddernetworks.mspaint.gui.window.search.SearchResult;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    private final MainGUI mainGUI;
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

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo-small.png").toString());
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
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

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
    private final AtomicBoolean currentlySearching = new AtomicBoolean(false);
    private final AtomicBoolean hasChanged = new AtomicBoolean(false);

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.searchResults.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        searchResults.setCellFactory(t -> new SearchListCell());

        CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    if (hasChanged.get()) {
                        lastChanged.set(System.currentTimeMillis());
                        alreadySearched.set(false);
                        hasChanged.set(false);
                    } else if (!alreadySearched.get() && !currentlySearching.get() && lastChanged.get() + 250 <= System.currentTimeMillis()) {
                        long start = System.currentTimeMillis();
                        currentlySearching.set(true);
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
                                currentlySearching.set(false);
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

                                    this.mainGUI.updateLoading(0, 1);
                                    this.mainGUI.setStatusText(null);

                                    alreadySearched.set(true);
                                    currentlySearching.set(false);
                                });
                            });
                        });
                    }

                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
        });

        this.searchText.textProperty().addListener(((observable, oldValue, newValue) -> hasChanged.set(true)));

        inFile.setSelected(true);
        action.setOnAction(event -> {
            action.setDisable(true);
            setError(null);
            String replaceText = this.replaceText.getText();

            if (replaceText.isEmpty()) {
                setError("The replace text is empty!");
                action.setDisable(false);
                return;
            }

            executorService.execute(() -> {
                this.searchResults.getSelectionModel().getSelectedItems()
                        .parallelStream()
                        .forEach(searchResult -> {
                            ReplaceManager replaceManager = new ReplaceManager(this.mainGUI);
                            try {
                                replaceManager.replaceText(searchResult, replaceText);
                            } catch (IOException | ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });

                Platform.runLater(() -> action.setDisable(false));
            });
        });

        replaceToggle.setSelected(initiallyReplace);
        if (!initiallyReplace) {
            replaceText.setDisable(true);
            action.setDisable(true);
        }

        replaceToggle.selectedProperty().addListener((observer, oldValue, newValue) -> {
            replaceText.setDisable(!newValue);
            action.setDisable(!newValue);
        });

        browse.setOnAction(event -> {
            if (inProject.isSelected()) return;
            boolean selectingFile = inFile.isSelected();

            FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, selectingFile ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY, file -> {
                directoryPath.setText(file.getAbsolutePath());
            });
        });

        Arrays.asList(inFile, inProject, inDirectory).forEach(radio ->
                radio.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                    hasChanged.set(true);
                })));

        cancel.setOnAction(event -> close());
    }
}

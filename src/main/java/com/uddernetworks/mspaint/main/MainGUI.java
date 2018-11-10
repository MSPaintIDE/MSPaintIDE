package com.uddernetworks.mspaint.main;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.git.GitController;
import com.uddernetworks.mspaint.imagestreams.TextPrintStream;
import com.uddernetworks.mspaint.install.Installer;
import com.uddernetworks.mspaint.languages.Language;
import com.uddernetworks.mspaint.main.gui.window.WelcomeWindow;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class MainGUI extends Application implements Initializable {

    @FXML
    private TextField inputName;
    @FXML
    private JFXTextField highlightedImage;
    @FXML
    private JFXTextField cacheFile;
    @FXML
    private JFXTextField classOutput;
    @FXML
    private JFXTextField compiledJarOutput;
    @FXML
    private JFXTextField libraryFile;
    @FXML
    private JFXTextField otherFiles;
    @FXML
    private JFXTextField compilerOutputValue;
    @FXML
    private JFXTextField programOutputValue;
    @FXML
    private JFXTextField originURL;
    @FXML
    private JFXPasswordField hiddenOriginURL;
    @FXML
    private JFXTextField commitMessage;

    @FXML
    private JFXButton changeInputImage;
    @FXML
    private JFXButton changeHighlightImage;
    @FXML
    private JFXButton changeCacheFile;
    @FXML
    private JFXButton changeClassOutput;
    @FXML
    private JFXButton changeCompiledJar;
    @FXML
    private JFXButton changeLibraries;
    @FXML
    private JFXButton changeOtherFiles;
    @FXML
    private JFXButton compilerOutput;
    @FXML
    private JFXButton programOutput;
    @FXML
    private JFXButton createRepo;
    @FXML
    private JFXButton addRemote;
    @FXML
    private JFXButton addFiles;
    @FXML
    private JFXButton commit;
    @FXML
    private JFXButton push;

    @FXML
    private JFXProgressBar progress;

    @FXML
    private Label statusText;

    @FXML
    private JFXCheckBox syntaxHighlight;
    @FXML
    private JFXCheckBox compile;
    @FXML
    private JFXCheckBox execute;
    @FXML
    private JFXCheckBox useCaches;
    @FXML
    private JFXCheckBox saveCaches;
    @FXML
    private JFXButton invertColors;
    @FXML
    private JFXButton remoteOriginVisibility;
    @FXML
    private JFXComboBox<Language> languageComboBox;

    @FXML
    private TextArea output;

    @FXML
    private AnchorPane rootAnchor;

    @FXML
    private MenuBar menu;

    private Main main;
    private Stage primaryStage;
    private boolean darkTheme = false;
    public static boolean HEADLESS = false;
    private boolean remoteURLVisible = true;
    private GitController gitController;
    private AtomicBoolean initialized = new AtomicBoolean();

    private FileFilter imageFilter = new FileNameExtensionFilter("Image files", "png");
    private FileFilter txtFilter = new FileNameExtensionFilter("Text document", "txt");
    private FileFilter jarFilter = new FileNameExtensionFilter("JAR Archive", "jar");

    public static final File LOCAL_MSPAINT = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");

    private ObservableList<Language> languages = FXCollections.observableArrayList();

    public MainGUI() throws IOException, URISyntaxException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.main = new Main();
        main.start(this);

        this.gitController = new GitController(this);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ReflectiveOperationException {
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            JFrame frame = new JFrame("MS Paint IDE");
            frame.setSize(700, 200);
            JPanel jPanel = new JPanel();
            jPanel.add(new JLabel("<html><br><br><div style='text-align: center;'>Sorry, MS Paint IDE only supports Windows<br> However, the developer of MS Paint IDE is going to be adding support soon. <br> Stay tuned</div><br></html>"));
            frame.add(jPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            return;
        }

        Installer installer = new Installer();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("install")) {
                installer.install();
                System.exit(0);
            } else if (args[0].equalsIgnoreCase("uninstall")) {
                installer.uninstall();
                System.exit(0);
            } else {
                HEADLESS = true;
                new TextEditorManager(args[0]);
                return;
            }
        }

        launch(args);
    }

    public void createAndOpenFile(File file) {
        try {
//            BufferedImage image = new BufferedImage(500, 600, BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < image.getWidth(); x++) {
//                for (int y = 0; y < image.getHeight(); y++) {
//                    image.setRGB(x, y, Color.WHITE.getRGB());
//                }
//            }
//
//            ImageIO.write(image, "png", new File(file.getAbsolutePath() + ".png"));
            file.createNewFile();
            new TextEditorManager(file, this);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showWelcomeScreen() throws IOException {
        this.primaryStage.hide();
        ProjectManager.closeCurrentProject();

        new WelcomeWindow(this, () -> {
            try {
                this.primaryStage.show();
                registerThings();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(100);

        Runnable ready = () -> {
            try {
                registerThings();
                primaryStage.setHeight(Math.min(primaryStage.getHeight(), GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getHeight() - 100));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        ProjectManager.getRecent();
        if (ProjectManager.getPPFProject() == null) {
            new WelcomeWindow(this, ready);
        } else {
            ready.run();
        }
    }

    public Main getMain() {
        return this.main;
    }

    public void setStatusText(String text) {
        Platform.runLater(() -> statusText.setText(text));
    }

    public void updateLoading(double current, double total) {
        Platform.runLater(() -> progress.setProgress(current / total));
    }

    public void setIndeterminate(boolean indeterminate) {
        resetError();
        Platform.runLater(() -> progress.setProgress(indeterminate ? -1 : 1));
    }

    public void registerThings() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this.primaryStage, root, false, true, true);
        jfxDecorator.setOnCloseButtonAction(() -> System.exit(0));
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("MS Paint IDE | " + ProjectManager.getPPFProject().getName());

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        this.primaryStage.setScene(scene);

        this.primaryStage.setTitle("MS Paint IDE | " + ProjectManager.getPPFProject().getName());
        this.primaryStage.getIcons().add(icon.getImage());

        this.primaryStage.show();
    }

    @FXML
    private void startScan(ActionEvent event) {
        new Thread(() -> {
            try {
                if (getCurrentLanguage() == null) {
                    setHaveError();
                    System.out.println("No language selected!");
                    return;
                }

                if (!getCurrentLanguage().meetsRequirements()) {
                    setHaveError();
                    System.out.println("You somehow selected a language that your\n" +
                            "system doesn't have the proper requirements for!");
                    return;
                }

                progress.setProgress(0);
                progress.getStyleClass().remove("progressError");

                long start = System.currentTimeMillis();
                if (main.indexAll(useCaches.isSelected(), saveCaches.isSelected()) == -1) return;

                if (syntaxHighlight.isSelected()) {
                    main.highlightAll();
                }

                if (compile.isSelected() || getCurrentLanguage().isInterpreted()) {
                    main.compile(execute.isSelected() || getCurrentLanguage().isInterpreted());
                }

                setStatusText("");
                updateLoading(0, 1);

                System.out.println("Finished everything in " + (System.currentTimeMillis() - start) + "ms");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean shouldUseCaches() {
        return this.useCaches.isSelected();
    }

    public boolean shouldSaveCaches() {
        return this.saveCaches.isSelected();
    }

    public void setHaveError() {
        Platform.runLater(() -> {
            progress.setProgress(1);
            progress.getStyleClass().remove("progressError");
            progress.getStyleClass().add("progressError");
            setStatusText("An error has occurred!");
        });
    }

    public void resetError() {
        Platform.runLater(() -> {
            progress.setProgress(0);
            progress.getStyleClass().remove("progressError");
        });
    }

    private void setGitFeaturesDisabled(boolean disabled) {
        createRepo.setDisable(disabled);
        addRemote.setDisable(disabled);
        addFiles.setDisable(disabled);
        commit.setDisable(disabled);
        push.setDisable(disabled);

        originURL.setDisable(disabled);
        hiddenOriginURL.setDisable(disabled);
        commitMessage.setDisable(disabled);
        remoteOriginVisibility.setDisable(disabled);
    }

    public void initializeInputTextFields() {
        PPFProject ppfProject = ProjectManager.getPPFProject();
        inputName.setText(getAbsolutePath(ppfProject.getInputLocation()));
        highlightedImage.setText(getAbsolutePath(ppfProject.getHighlightLocation()));
        cacheFile.setText(getAbsolutePath(ppfProject.getObjectLocation()));
        compiledJarOutput.setText(getAbsolutePath(ppfProject.getJarFile()));
        libraryFile.setText(getAbsolutePath(ppfProject.getLibraryLocation()));
        otherFiles.setText(getAbsolutePath(ppfProject.getObjectLocation()));
        classOutput.setText(getAbsolutePath(ppfProject.getClassLocation()));
        compilerOutputValue.setText(getAbsolutePath(ppfProject.getCompilerOutput()));
        programOutputValue.setText(getAbsolutePath(ppfProject.getAppOutput()));
    }

    private String getAbsolutePath(File file) {
        if (file == null) return "";
        return file.getAbsolutePath();
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public boolean useDarkTheme() {
        return this.darkTheme;
    }

    public void updateTheme() {
        if (this.initialized.get()) {
            Platform.runLater(() -> {
                Parent parent = invertColors.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
                parent.lookupAll(".theme-text").forEach(node -> {
                    if (this.darkTheme) {
                        node.getStyleClass().add("dark-text");
                    } else {
                        node.getStyleClass().remove("dark-text");
                    }
                });

                if (this.darkTheme) {
                    parent.lookupAll(".gridpane-theme").stream().map(Node::getStyleClass).forEach(classes -> classes.add("gridpane-theme-dark"));
                    parent.lookup(".output-theme").getStyleClass().add("output-theme-dark");
                    parent.lookup(".invert-colors").getStyleClass().add("invert-colors-white");
                    parent.lookup(".remote-origin-visibility").getStyleClass().add("dark");
                    parent.lookup(".language-selection").getStyleClass().add("language-selection-dark");
                    this.menu.getStyleClass().add("menubar-dark");
                } else {
                    parent.lookupAll(".gridpane-theme").stream().map(Node::getStyleClass).forEach(classes -> classes.remove("gridpane-theme-dark"));
                    parent.lookup(".output-theme").getStyleClass().remove("output-theme-dark");
                    parent.lookup(".invert-colors").getStyleClass().remove("invert-colors-white");
                    parent.lookup(".remote-origin-visibility").getStyleClass().remove("dark");
                    parent.lookup(".language-selection").getStyleClass().remove("language-selection-dark");
                    this.menu.getStyleClass().remove("menubar-dark");
                }
            });
        }
    }

    public void addLanguages(List<Language> languages) {
        this.languages.addAll(languages);
    }

    public ObservableList<Language> getLanguages() {
        return languages;
    }

    public Language getCurrentLanguage() {
        return this.main.getCurrentLanguage();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeInputTextFields();
        setGitFeaturesDisabled(true);
        this.initialized.set(true);
        this.languageComboBox.setItems(languages);

        menu.getMenus()
                .stream()
                .filter(MaterialMenu.class::isInstance)
                .map(MaterialMenu.class::cast)
                .flatMap(menu -> Stream.concat(menu.getItems()
                        .stream()
                        .filter(MaterialMenu.class::isInstance)
                        .map(MaterialMenu.class::cast), Stream.of(menu)))
                .forEach(materialMenu -> materialMenu.initialize(this));

        inputName.textProperty().addListener(event -> main.setInputImage(new File(inputName.getText())));

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Error happened! Thread " + thread + " exception: " + exception.getLocalizedMessage());
            exception.printStackTrace();
            setHaveError();
        });

        TextPrintStream textPrintStream = new TextPrintStream(output, System.out);
        PrintStream textOut = new PrintStream(textPrintStream);
        System.setOut(textOut);
        System.setErr(textOut);

        invertColors.setOnAction(event -> {
            SettingsManager.setSetting(Setting.DARK_THEME, this.darkTheme = !this.darkTheme);
            updateTheme();
        });

        updateTheme();

        hiddenOriginURL.setManaged(false);
        hiddenOriginURL.setVisible(false);

        originURL.textProperty().bindBidirectional(hiddenOriginURL.textProperty());

        remoteOriginVisibility.setOnAction(event -> {
            Parent parent = invertColors.getParent().getParent().getParent().getParent().getParent().getParent();
            this.gitController.setHideOrigin(!(remoteURLVisible ^= true));

            originURL.setManaged(remoteURLVisible);
            originURL.setVisible(remoteURLVisible);

            hiddenOriginURL.setManaged(!remoteURLVisible);
            hiddenOriginURL.setVisible(!remoteURLVisible);

            if (remoteURLVisible) {
                originURL.requestFocus();
                originURL.selectEnd();
                parent.lookup(".remote-origin-visibility").getStyleClass().remove("off");
            } else {
                hiddenOriginURL.requestFocus();
                hiddenOriginURL.selectEnd();
                parent.lookup(".remote-origin-visibility").getStyleClass().add("off");
            }
        });

        languageComboBox.setOnAction(event -> {
            Language language = languageComboBox.getSelectionModel().getSelectedItem();
            this.main.setCurrentLanguage(language);
            compile.setDisable(language.isInterpreted());
        });

        this.gitController.getVersion(gitVersion -> {
            if (gitVersion == null) {
                System.out.println("Git not found! Git features will not be available.");
            } else {
                System.out.println("Git found! Version: " + gitVersion);
                setGitFeaturesDisabled(false);
            }
        });

        createRepo.setOnAction(event -> this.gitController.gitInit(ProjectManager.getPPFProject().getInputLocation()));

        addFiles.setOnAction(event -> FileDirectoryChooser.openMultiFileChoser(ProjectManager.getPPFProject().getInputLocation(), null, JFileChooser.FILES_AND_DIRECTORIES, files -> {
            try {
                this.gitController.addFiles(files);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        addRemote.setOnAction(event -> this.gitController.setRemoteOrigin(originURL.getText()));

        commit.setOnAction(event -> {
            try {
                this.gitController.commit(commitMessage.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        push.setOnAction(event -> this.gitController.push());

        inputName.textProperty().addListener(event -> main.setInputImage(inputName.getText().trim().isEmpty() ? null : new File(inputName.getText())));

        changeInputImage.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getInputLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getInputLocation();
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                inputName.setText(file.getAbsolutePath());
                main.setInputImage(file);
            });
        });

        highlightedImage.textProperty().addListener(event -> ProjectManager.getPPFProject().setHighlightLocation(highlightedImage.getText().trim().isEmpty() ? null : new File(highlightedImage.getText())));

        changeHighlightImage.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getHighlightLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getHighlightLocation();
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                highlightedImage.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setHighlightLocation(file);
            });
        });

        cacheFile.textProperty().addListener(event -> ProjectManager.getPPFProject().setObjectLocation(cacheFile.getText().trim().isEmpty() ? null : new File(cacheFile.getText())));

        changeCacheFile.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getObjectLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getObjectLocation();
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                cacheFile.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setObjectLocation(file);
            });
        });

        classOutput.textProperty().addListener(event -> ProjectManager.getPPFProject().setClassLocation(classOutput.getText().trim().isEmpty() ? null : new File(classOutput.getText())));

        changeClassOutput.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getClassLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getClassLocation();
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                classOutput.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setClassLocation(file);
            });
        });

        compiledJarOutput.textProperty().addListener(event -> ProjectManager.getPPFProject().setJarFile(compiledJarOutput.getText().trim().isEmpty() ? null : new File(compiledJarOutput.getText())));

        changeCompiledJar.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getJarFile() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getJarFile();
            FileDirectoryChooser.openFileChooser(selected, jarFilter, JFileChooser.FILES_ONLY, file -> {
                compiledJarOutput.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setJarFile(file);
            });
        });

        libraryFile.textProperty().addListener(event -> ProjectManager.getPPFProject().setLibraryLocation(libraryFile.getText().trim().isEmpty() ? null : new File(libraryFile.getText())));

        changeLibraries.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getLibraryLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getLibraryLocation();
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                libraryFile.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setLibraryLocation(file);
            });
        });

        otherFiles.textProperty().addListener(event -> ProjectManager.getPPFProject().setOtherLocation(otherFiles.getText().trim().isEmpty() ? null : new File(otherFiles.getText())));

        changeOtherFiles.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getOtherLocation() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getOtherLocation();
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                otherFiles.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setOtherLocation(file);
            });
        });

        compilerOutputValue.textProperty().addListener(event -> ProjectManager.getPPFProject().setCompilerOutput(compilerOutputValue.getText().trim().isEmpty() ? null : new File(compilerOutputValue.getText())));

        compilerOutput.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getCompilerOutput() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getCompilerOutput();
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                compilerOutputValue.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setCompilerOutput(file);
            });
        });

        programOutputValue.textProperty().addListener(event -> ProjectManager.getPPFProject().setAppOutput(programOutputValue.getText().trim().isEmpty() ? null : new File(programOutputValue.getText())));

        programOutput.setOnAction(event -> {
            File selected = ProjectManager.getPPFProject().getAppOutput() == null ? ProjectManager.getPPFProject().getJarFile() : ProjectManager.getPPFProject().getAppOutput();
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                programOutputValue.setText(file.getAbsolutePath());
                ProjectManager.getPPFProject().setAppOutput(file);
            });
        });
    }

    public TextArea getOutputTextArea() {
        return output;
    }
}

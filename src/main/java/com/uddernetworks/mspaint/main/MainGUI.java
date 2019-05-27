package com.uddernetworks.mspaint.main;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.LangGUIOptionRequirement;
import com.uddernetworks.mspaint.code.OverrideExecute;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.gui.LangGUIOption;
import com.uddernetworks.mspaint.git.GitController;
import com.uddernetworks.mspaint.gui.MaterialMenu;
import com.uddernetworks.mspaint.gui.window.WelcomeWindow;
import com.uddernetworks.mspaint.logging.GUIConsoleAppender;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.splash.Splash;
import com.uddernetworks.mspaint.splash.SplashMessage;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MainGUI extends Application implements Initializable {

    private static Logger LOGGER;

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
    private JFXButton generate;
    @FXML
    private JFXButton addRemote;
    @FXML
    private JFXButton addFiles;
    @FXML
    private JFXButton commit;
    @FXML
    private JFXButton push;

    @FXML
    private JFXButton start;

    @FXML
    private JFXProgressBar progress;

    @FXML
    private Label statusText;

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
    private GridPane langSettingsGrid;

    @FXML
    private FlowPane checkboxFlow;

    @FXML
    private MenuBar menu;

    private StartupLogic startupLogic;
    private Stage primaryStage;
    private boolean darkTheme = false;
    public static boolean HEADLESS = false;
    private boolean remoteURLVisible = true;
    private GitController gitController;
    private AtomicBoolean initialized = new AtomicBoolean();
    private static File initialProject = null;
    private ThemeManager themeManager;
    private boolean runListeners = true;

    private Map<String, Image> cachedTaksbarIcons = new HashMap<>();
    private Map<String, ImageView> cachedImageViews = new HashMap<>();

    public static File APP_DATA = new File(System.getenv("LocalAppData"), "MSPaintIDE");

    private ObservableList<Language> languages = FXCollections.observableArrayList();

    public MainGUI() throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        System.setProperty("jna.debug_load", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.startupLogic = new StartupLogic();
        startupLogic.start(this);

        this.gitController = new GitController(this);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // See com.sun.glass.ui.Application#checkEventThread() javadoc
//        System.setProperty("glass.disableThreadChecks", "true");

//        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
//            System.out.println("Exception on " + t.getName());
//            System.out.println(e.getLocalizedMessage());
//            System.out.println(e.getMessage());
//            for (var stackTraceElement : e.getStackTrace()) {
//                System.out.println(stackTraceElement.toString());
//            }
//            e.printStackTrace(System.out);
//            e.printStackTrace();
//        });


//        var fiule = new File("C:\\Users\\RubbaBoy\\AppData\\Local\\MSPaintIDE\\shit.txt");
//        fiule.mkdirs();
//        fiule.createNewFile();
//        PrintStream o = new PrintStream(fiule);
//        System.setOut(o);
        System.setProperty("logPath", APP_DATA.getAbsolutePath() + "\\log");
        LOGGER = LoggerFactory.getLogger(MainGUI.class);

        System.out.println("111 java.library.path = ");
        System.out.println(System.getProperty("java.library.path"));

        System.setProperty("java.library.path", System.getProperty("java.library.path") + ";" + System.getenv("PATH"));

        System.out.println("222 java.library.path = ");
        System.out.println(System.getProperty("java.library.path"));

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
        }

//        new Splash();

        if (args.length == 1) {
            if (args[0].endsWith(".ppf")) {
                initialProject = new File(args[0]);
                if (!initialProject.isFile()) initialProject = null;
            } else {
                HEADLESS = true;
                new TextEditorManager(args[0]);
                return;
            }
        }

        new Splash();

        launch(args);
    }

    public void createAndOpenTextFile(File file) {
        try {
            setIndeterminate(true);
            file.createNewFile();
            new TextEditorManager(file, this);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        progress.setProgress(0);
    }

    public void createAndOpenImageFile(File file) {
        try {
            if (!file.getName().endsWith(".png")) file = new File(file.getAbsolutePath() + ".png");
            setIndeterminate(true);

            BufferedImage image = new BufferedImage(600, 500, BufferedImage.TYPE_INT_ARGB);
            clearImage(image);
            ImageIO.write(image, "png", file);

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mspaint.exe \"" + file.getAbsolutePath() + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateLoading(0, 1);
    }

    private void clearImage(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

    public void showWelcomeScreen() throws IOException {
        this.primaryStage.hide();
        ProjectManager.closeCurrentProject();

        new WelcomeWindow(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(100);

        ProjectManager.getRecent();

        System.out.println("Reading 111 " + initialProject);
        if (initialProject != null) ProjectManager.switchProject(ProjectManager.readProject(initialProject));
        if (ProjectManager.getPPFProject() == null) {
            new WelcomeWindow(this);
            Splash.end();
        } else {
            refreshProject();
        }
    }

    public void refreshProject() {
        String languageClassString = ProjectManager.getPPFProject().getLanguage();
        Platform.runLater(() -> {
            try {
                var langClass = Class.forName(languageClassString);
                var languageManager = this.startupLogic.getLanguageManager();

                LOGGER.info(langClass.getCanonicalName());

                languageManager.getLanguageByClass(langClass).ifPresent(language -> {
                    this.startupLogic.setCurrentLanguage(language);
                    language.loadForCurrent();
                });
                languageManager.reloadAllLanguages();

                registerThings();
                primaryStage.setHeight(Math.min(primaryStage.getHeight(), GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getHeight() - 100));

                setSelectedLanguage(langClass);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                LOGGER.error("No language found with a class of \"" + languageClassString + "\"");
            }
        });
    }

    public StartupLogic getStartupLogic() {
        return this.startupLogic;
    }

    public void setStatusText(String text) {
        Platform.runLater(() -> statusText.setText(text));
    }

    public GitController getGitController() {
        return this.gitController;
    }

    public void updateLoading(double current, double total) {
        Platform.runLater(() -> progress.setProgress(current / total));
    }

    public void setIndeterminate(boolean indeterminate) {
        resetError();
        Platform.runLater(() -> progress.setProgress(indeterminate ? -1 : 1));
    }

    private boolean registeredBefore = false;

    public void registerThings() throws IOException {
        System.out.println("MainGUI.registerThings");

        if (this.registeredBefore) {
            System.out.println("Already registered, so just showing...");
            this.primaryStage.show();
        } else {
            System.out.println("Else!");
            this.registeredBefore = true;
            if (!this.primaryStage.isShowing()) {
                System.out.println("Not showing now");

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/Main.fxml"));
                loader.setController(this);
                Parent root = loader.load();

                JFXDecorator jfxDecorator = new JFXDecorator(this.primaryStage, root, false, true, true);
                jfxDecorator.setOnCloseButtonAction(() -> System.exit(0));

                Scene scene = new Scene(jfxDecorator);
                scene.getStylesheets().add("style.css");

                this.primaryStage.setScene(scene);

                getThemeManager();
                this.themeManager.addStage(this.primaryStage);

                var settingsManager = SettingsManager.getInstance();

                // Built-in themes
                settingsManager.<String, String>getSettingMap(Setting.THEMES).forEach(this.themeManager::loadTheme);

                this.themeManager.init();

                settingsManager.<String>onChangeSetting(Setting.TASKBAR_ICON, icon -> {
                    String path = "";
                    switch (icon) {
                        case "Colored":
                            path = "ms-paint-logo-colored.png";
                            break;
                        case "White":
                            path = "ms-paint-logo-white.png";
                            break;
                        case "Black":
                            path = "ms-paint-logo.png";
                            break;
                    }

                    changeImage(path);
                }, true);
            }
        }

        ((JFXDecorator) this.primaryStage.getScene().getRoot()).setTitle("MS Paint IDE | " + ProjectManager.getPPFProject().getName());

        this.primaryStage.setTitle("MS Paint IDE | " + ProjectManager.getPPFProject().getName());

        Splash.setStatus(SplashMessage.STARTING);
        this.primaryStage.setOnShown(event -> Splash.end());
        System.out.println("Here!");
        if (!this.primaryStage.isShowing()) {
            System.out.println("#show!");
            this.primaryStage.show();
        }
    }

    public void changeImage(String path) {
        List<Image> icons = this.primaryStage.getIcons();
        icons.clear();
        icons.add(this.cachedTaksbarIcons.computeIfAbsent(path, path2 -> new Image(getClass().getClassLoader().getResourceAsStream("icons/taskbar/" + path))));

        JFXDecorator root = (JFXDecorator) this.primaryStage.getScene().getRoot();
        root.setGraphic(this.cachedImageViews.computeIfAbsent(path, path2 -> {
            ImageView imageView = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/" + path).toString());
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);
            return imageView;
        }));
    }

    public ObservableList<String> getStylesheets() {
        return this.primaryStage.getScene().getStylesheets();
    }

    public void fullCompile(OverrideExecute executeOverride) {
        try {
            var language = this.startupLogic.getCurrentLanguage();
            if (getCurrentLanguage() == null) {
                setHaveError();
                LOGGER.error("No language selected!");
                return;
            }

            if (!getCurrentLanguage().meetsRequirements()) {
                setHaveError();
                LOGGER.error("You somehow selected a language that your system doesn't have the proper requirements for!");
                return;
            }

            progress.setProgress(0);
            progress.getStyleClass().remove("progressError");

            long start = System.currentTimeMillis();

            var imageClassesOptional = language.indexFiles();
            if (imageClassesOptional.isPresent()) {
                var imageClasses = imageClassesOptional.get();

                imageClasses.forEach(ImageClass::scan);

                language.highlightAll(imageClasses);

                // TODO: Skip step in inapplicable languages?
                startupLogic.compile(imageClasses, executeOverride);
            } else {
                LOGGER.error("Error while finding ImageClasses, aborting...");
            }

            setStatusText("");
            updateLoading(0, 1);

            LOGGER.info("Finished everything in " + (System.currentTimeMillis() - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public boolean useDarkTheme() {
        return this.darkTheme;
    }

    public void updateTheme() {
        if (this.initialized.get()) {
            Platform.runLater(() -> getThemeManager().onDarkThemeChange(rootAnchor, Map.of(
                    ".gridpane-theme", "gridpane-theme-dark",
                    ".output-theme", "output-theme-dark",
                    ".invert-colors", "invert-colors-white",
                    ".remote-origin-visibility", "dark",
                    ".language-selection", "language-selection-dark",
                    ".vbox-theme", "vbox-theme-dark",
                    "#menu", "menubar-dark"
            )));
        }
    }

    public void addLanguages(List<Language> languages) {
        this.languages.addAll(languages);
    }

    public ObservableList<Language> getLanguages() {
        return languages;
    }

    public Language getCurrentLanguage() {
        return this.startupLogic.getCurrentLanguage();
    }

    public String getOrigin() {
        return this.originURL.getText();
    }

    public void setSelectedLanguage(Class<?> languageClass) {
        var items = this.languageComboBox.getItems();
        var selectionModel = this.languageComboBox.getSelectionModel();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getClass().equals(languageClass)) {
                selectionModel.clearAndSelect(i);
            }
        }
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var settingsManager = SettingsManager.getInstance();
        Splash.setStatus(SplashMessage.GUI);

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

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            LOGGER.error("Error happened on thread " + thread.getName(), exception);
            setHaveError();
        });

        GUIConsoleAppender.activate(output);

        invertColors.setOnAction(event -> {
            settingsManager.setSetting(Setting.DARK_THEME, this.darkTheme = !this.darkTheme);
            updateTheme();
        });

        updateTheme();

        start.setOnAction(event -> fullCompile(OverrideExecute.DEFAULT));

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
            this.startupLogic.setCurrentLanguage(language);

            language.loadForCurrent();
            ProjectManager.getPPFProject().setLanguage(language.getClass().getCanonicalName());
            ProjectManager.save();
        });

        this.gitController.getVersion(gitVersion -> {
            if (gitVersion == null) {
                LOGGER.warn("Git not found! Git features will not be available.");
            } else {
                LOGGER.info("Git found! Version: " + gitVersion);
                setGitFeaturesDisabled(false);
            }
        });

        generate.setOnAction(event -> {
            PPFProject project = ProjectManager.getPPFProject();

            this.startupLogic.getCurrentLanguage().getLanguageSettings().generateDefaults();

            if (project.getActiveFont() == null) {
                project.addFont("Comic Sans MS", "fonts/ComicSans");
                project.addFont("Monospaced.plain", "fonts/Monospaced.plain");
                project.addFont("Verdana", "fonts/Verdana");
                project.addFont("Courier New", "fonts/CourierNew");
                project.addFont("Consolas", "fonts/Consolas");
                project.addFont("Calibri", "fonts/Calibri");
                project.setActiveFont("Comic Sans MS");
            }

            if (settingsManager.getSettingMap(Setting.THEMES).isEmpty()) {
                settingsManager.setSetting(Setting.THEMES, Map.of(
                        "Default", "themes/default.css",
                        "Extra Dark", "themes/extra-dark.css"
                ));
            }

            var trainImage = settingsManager.getSetting(Setting.TRAIN_IMAGE);
            if (trainImage == null || ((String) trainImage).trim().equals("")) {
                settingsManager.setSetting(Setting.TRAIN_IMAGE, project.getFile().getParentFile().getAbsolutePath() + "\\train.png");
            }

            ProjectManager.save();
        });

        createRepo.setOnAction(event -> this.gitController.gitInit(ProjectManager.getPPFProject().getFile()));

        addFiles.setOnAction(event -> FileDirectoryChooser.openMultiFileSelector(chooser ->
                chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile()), files -> {
            try {
                this.gitController.addFiles(files.toArray(File[]::new));
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

        Function<Runnable, JFXButton> buttonGen = callback -> {
            var button = new JFXButton();
            button.setText("Change");
            button.setOnAction(event -> callback.run());
            button.setButtonType(JFXButton.ButtonType.RAISED);
            button.setPrefWidth(75);
            button.setPrefHeight(30);
            button.getStyleClass().add("primary-button");
            GridPane.setColumnIndex(button, 2);
            GridPane.setHalignment(button, HPos.CENTER);
            GridPane.setValignment(button, VPos.CENTER);
            return button;
        };

        var langSettings = getCurrentLanguage().getLanguageSettings();

        var i = new LongAdder();
        langSettings.getOptionsGUI(Predicate.not(Predicate.isEqual(LangGUIOptionRequirement.BOTTOM_DISPLAY)))
                .stream()
                .sorted(Comparator.comparingInt(LangGUIOption::getIndex))
                .forEach(langGUIOption -> {
            var childrenAdding = new ArrayList<>(Arrays.asList(langGUIOption.getTextControl(), langGUIOption.getDisplay()));
            if (langGUIOption.hasChangeButton()) childrenAdding.add(buttonGen.apply(langGUIOption::activateChangeButtonAction));
            langSettingsGrid.addRow(i.intValue(), childrenAdding.toArray(Control[]::new));
            i.increment();
        });

        Function<LangGUIOption, JFXCheckBox> checkBoxGen = option -> {
            var checkbox = new JFXCheckBox(option.getName());
            checkbox.setMnemonicParsing(false);
            checkbox.setStyle("-jfx-checked-color: -primary-button-color;");
            checkbox.getStyleClass().add("theme-text");
            checkbox.setCursor(Cursor.HAND);
            checkbox.setPadding(new Insets(0, 10, 0, 0));
            System.out.println(option.getName() + " = " + option.getSetting());
            checkbox.selectedProperty().bindBidirectional((BooleanProperty) option.getProperty());
//            checkbox.setSelected((Boolean) option.getSetting());
//            checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> callback.accept(newValue));
            return checkbox;
        };

        langSettings.getOptionsGUI(Predicate.isEqual(LangGUIOptionRequirement.BOTTOM_DISPLAY)).forEach(guiOption ->
                checkboxFlow.getChildren().add(checkBoxGen.apply(guiOption)));
    }

    public TextArea getOutputTextArea() {
        return output;
    }

    public ThemeManager getThemeManager() {
        if (this.themeManager == null) this.themeManager = new ThemeManager();
        return themeManager;
    }
}

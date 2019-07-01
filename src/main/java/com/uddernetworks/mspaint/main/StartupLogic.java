package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.GeneralRunningCodeManager;
import com.uddernetworks.mspaint.code.execution.RunningCodeManager;
import com.uddernetworks.mspaint.code.highlighter.AngrySquiggleHighlighter;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageManager;
import com.uddernetworks.mspaint.code.languages.java.JavaLanguage;
import com.uddernetworks.mspaint.gui.window.diagnostic.DefaultDiagnosticManager;
import com.uddernetworks.mspaint.gui.window.diagnostic.DiagnosticManager;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.ocr.OCRManager;
import com.uddernetworks.mspaint.painthook.InjectionManager;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.splash.Splash;
import com.uddernetworks.mspaint.splash.SplashMessage;
import com.uddernetworks.mspaint.texteditor.CenterPopulator;
import com.uddernetworks.mspaint.watcher.DefaultFileWatchManager;
import com.uddernetworks.mspaint.watcher.FileWatchManager;
import com.uddernetworks.paintassist.DefaultPaintAssist;
import com.uddernetworks.paintassist.PaintAssist;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static java.util.stream.Collectors.*;

public class StartupLogic {

    private static Logger LOGGER = LoggerFactory.getLogger(StartupLogic.class);

    private static File currentJar;

    private MainGUI mainGUI;

    private LanguageManager languageManager = new LanguageManager();
    private Language currentLanguage;
    private RunningCodeManager runningCodeManager;
    private OCRManager ocrManager;
    private PaintAssist paintAssist;
    private FileWatchManager fileWatchManager;
    private DiagnosticManager diagnosticManager;

    private CenterPopulator centerPopulator;

    private Method addURL;
    private List<String> added = new ArrayList<>();

    public void start(MainGUI mainGUI) throws IOException {
        this.fileWatchManager = new DefaultFileWatchManager();
        headlessStart();
        this.mainGUI = mainGUI;

        addPath(MainGUI.APP_DATA.getAbsolutePath());
        new File(MainGUI.APP_DATA, "fonts").mkdirs();
        new File(MainGUI.APP_DATA, "themes").mkdirs();

        this.mainGUI.setDarkTheme(SettingsManager.getInstance().getSetting(Setting.DARK_THEME));
        this.mainGUI.updateTheme();

        Splash.setStatus(SplashMessage.ADDING_LANGUAGES);

        languageManager.addLanguage(new JavaLanguage(this));
//        languageManager.addLanguage(new BrainfuckLanguage(this));
//        languageManager.addLanguage(new PythonLanguage());

        languageManager.initializeLanguages();
        mainGUI.addLanguages(languageManager.getEnabledLanguages());

        this.runningCodeManager = new GeneralRunningCodeManager(this);

        new InjectionManager(mainGUI, this).createHooks();
        this.paintAssist = new DefaultPaintAssist();

        List<ImageClass> hasErrors = new ArrayList<>();

        (this.diagnosticManager = new DefaultDiagnosticManager(this)).onDiagnosticChange(entries -> {
            var documentManager = this.currentLanguage.getLSPWrapper().getDocumentManager();
            var sortedDiagnostics = entries.stream().collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
            var thisAdded = new ArrayList<ImageClass>();
            sortedDiagnostics.forEach((uri, diagnostics) -> {
                try {
                    var document = documentManager.getDocument(new File(URI.create(uri + ".png")));
                    var imageClass = document.getImageClass();
                    thisAdded.add(imageClass);
                    imageClass.getScannedImage().ifPresentOrElse(img -> {}, () -> LOGGER.error("Error! Image has not been scanned yet! {}", uri));
                    this.currentLanguage.highlightAll(Collections.singletonList(imageClass));
                    AngrySquiggleHighlighter highlighter = new AngrySquiggleHighlighter(mainGUI.getStartupLogic(), imageClass, 1 / 6D, imageClass.getHighlightedFile(), imageClass.getScannedImage().get(), diagnostics);
                    highlighter.highlightAngrySquiggles();
                } catch (IOException | TranscoderException e) {
                    LOGGER.error("Error while writing diagnostics to " + uri, e);
                }
            });

            hasErrors.removeAll(thisAdded);

            try {
                this.currentLanguage.highlightAll(hasErrors);
            } catch (IOException e) {
                LOGGER.error("An error occurred while highlighting images", e);
            }

            hasErrors.clear();
            hasErrors.addAll(thisAdded);
        });
    }

    public void headlessStart() throws IOException {
        LOGGER.info("Loading settings");
        Splash.setStatus(SplashMessage.SETTINGS);
        var optionsFile = new File(MainGUI.APP_DATA, "options.ini");
        var initializeSettings = !optionsFile.exists();
        var settingsManager = SettingsManager.getInstance();
        settingsManager.initialize(optionsFile);
        this.centerPopulator = new CenterPopulator(this);

        Splash.setStatus(SplashMessage.DATABASE);
        this.ocrManager = new OCRManager(this);

        if (initializeSettings) {
            settingsManager.setSetting(Setting.THEMES, Map.of(
                    "Default", "themes/default.css",
                    "Extra Dark", "themes/extra-dark.css"
            ));

            if (!MainGUI.HEADLESS && ProjectManager.getPPFProject() != null) {
                settingsManager.setSetting(Setting.TRAIN_IMAGE, ProjectManager.getPPFProject().getFile().getParentFile().getAbsolutePath() + "\\train.png");
            }
        }

        if (MainGUI.HEADLESS) {
            settingsManager.<String>onChangeSetting(Setting.HEADLESS_FONT, font ->
                    this.ocrManager.setActiveFont(font, settingsManager.getSetting(Setting.HEADLESS_FONT_CONFIG)), true);
        } else {
            ProjectManager.switchProjectConsumer(project -> {
                if (project.getActiveFont() == null) {
                    project.addFont(settingsManager.getSetting(Setting.HEADLESS_FONT), settingsManager.getSetting(Setting.HEADLESS_FONT_CONFIG));
                    project.setActiveFont(settingsManager.getSetting(Setting.HEADLESS_FONT));
                }

                project.onFontUpdate((name, path) -> this.ocrManager.setActiveFont(name, path), true);
            });
        }
    }

    public void setCurrentLanguage(Language language) {
        this.currentLanguage = language;
    }

    public Language getCurrentLanguage() {
        return this.currentLanguage;
    }

    // TODO: Move to different class?
    public void compile(List<ImageClass> imageClasses, BuildSettings buildSettings) throws IOException {
        long start = System.currentTimeMillis();

        if (this.currentLanguage.isInterpreted()) {
            LOGGER.info("Interpreting...");
            mainGUI.setStatusText("Interpreting...");
        } else {
            LOGGER.info("Compiling...");
            mainGUI.setStatusText("Compiling...");
        }

        mainGUI.setIndeterminate(true);

        var imageOutputStream = new ImageOutputStream(this, this.currentLanguage.getAppOutput(), 500);
        var compilerOutputStream = new ImageOutputStream(this, this.currentLanguage.getCompilerOutput(), 500);

        var result = this.currentLanguage.compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerOutputStream, buildSettings);

        if (result.getCompletionStatus() == CompilationResult.Status.RUNNING) {
            this.runningCodeManager.getRunningCode().ifPresentOrElse(runningCode -> {
                runningCode.afterAll((exitCode, ignored) -> {
                    LOGGER.info("Saving program output images...");
                    mainGUI.setStatusText("Saving program output images...");

                    imageOutputStream.saveImage();

                    mainGUI.setStatusText("");
                });
            }, () -> LOGGER.error("Completion status is RUNNING however no RunningCode has been found..."));
        }

        LOGGER.info("Finished " + (getCurrentLanguage().isInterpreted() ? "interpreting" : "compiling") + " in " + (System.currentTimeMillis() - start) + "ms");

        LOGGER.info("Saving compiler output images...");
        mainGUI.setStatusText("Saving compiler output images...");

        compilerOutputStream.saveImage();

        mainGUI.setStatusText(null);
    }

    public void addPath(String path) {
        try {
            if (this.added.contains(path)) return;
            if (this.addURL == null) {
                this.addURL = ClassLoader.getSystemClassLoader().getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                this.addURL.setAccessible(true);
            }

            this.addURL.invoke(ClassLoader.getSystemClassLoader(), path);
            this.added.add(path);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static Optional<File> getCurrentJar() {
        if (currentJar != null) return Optional.of(currentJar);
        try {
            return Optional.of((currentJar = new File(StartupLogic.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<File> getJarParent() {
        try {
            var currentJar = new File(StartupLogic.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            var numBack = currentJar.getParentFile().getName().equals("app") ? 2 : 4;
            return Optional.of(getParentsBack(currentJar, numBack));
        } catch (URISyntaxException e) {
            LOGGER.error("Error getting URI of file", e);
        }

        return Optional.empty();
    }

    public static File getParentsBack(File base, int back) {
        for (int i = 0; i < back; i++) base = base.getParentFile();
        return base;
    }

    public MainGUI getMainGUI() {
        return mainGUI;
    }

    public OCRManager getOCRManager() {
        return ocrManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public RunningCodeManager getRunningCodeManager() {
        return runningCodeManager;
    }

    public CenterPopulator getCenterPopulator() {
        return centerPopulator;
    }

    public PaintAssist getPaintAssist() {
        return paintAssist;
    }

    public String getFontName() {
        return MainGUI.HEADLESS ? SettingsManager.getInstance().getSetting(Setting.HEADLESS_FONT) : ProjectManager.getPPFProject().getActiveFont();
    }

    public String getFontConfig() {
        return MainGUI.HEADLESS ? SettingsManager.getInstance().getSetting(Setting.HEADLESS_FONT_CONFIG) : ProjectManager.getPPFProject().getActiveFontConfig();
    }

    public FileWatchManager getFileWatchManager() {
        return fileWatchManager;
    }

    public DiagnosticManager getDiagnosticManager() {
        return diagnosticManager;
    }
}

package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.GeneralRunningCodeManager;
import com.uddernetworks.mspaint.code.execution.RunningCodeManager;
import com.uddernetworks.mspaint.code.highlighter.AngrySquiggleHighlighter;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageManager;
import com.uddernetworks.mspaint.code.languages.golang.GoLanguage;
import com.uddernetworks.mspaint.code.languages.java.JavaLanguage;
import com.uddernetworks.mspaint.code.languages.javascript.JSLanguage;
import com.uddernetworks.mspaint.code.languages.python.PythonLanguage;
import com.uddernetworks.mspaint.discord.DiscordRPCManager;
import com.uddernetworks.mspaint.discord.RPCManager;
import com.uddernetworks.mspaint.gui.window.diagnostic.DefaultDiagnosticManager;
import com.uddernetworks.mspaint.gui.window.diagnostic.DiagnosticManager;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.ocr.OCRManager;
import com.uddernetworks.mspaint.painthook.InjectionManager;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.texteditor.CenterPopulator;
import com.uddernetworks.mspaint.util.Browse;
import com.uddernetworks.mspaint.watcher.DefaultFileWatchManager;
import com.uddernetworks.mspaint.watcher.FileWatchManager;
import com.uddernetworks.paintassist.DefaultPaintAssist;
import com.uddernetworks.paintassist.PaintAssist;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

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
    private List<RPCManager> rpcManagers = new ArrayList<>();

    private CenterPopulator centerPopulator;

    private Method addURL;
    private List<String> added = new ArrayList<>();

    public void start(MainGUI mainGUI) throws IOException {
        this.rpcManagers.add(new DiscordRPCManager());
        runRPC(RPCManager::init);

        this.fileWatchManager = new DefaultFileWatchManager();
        headlessStart();
        this.mainGUI = mainGUI;

        addPath(MainGUI.APP_DATA.getAbsolutePath());
        new File(MainGUI.APP_DATA, "fonts").mkdirs();
        new File(MainGUI.APP_DATA, "themes").mkdirs();

        this.mainGUI.setDarkTheme(SettingsManager.getInstance().getSetting(Setting.DARK_THEME));
        this.mainGUI.updateTheme();

        languageManager.addLanguage(new JavaLanguage(this));
        languageManager.addLanguage(new PythonLanguage(this));
        languageManager.addLanguage(new GoLanguage(this));
        languageManager.addLanguage(new JSLanguage(this));

        languageManager.initializeLanguages();
        mainGUI.addLanguages(languageManager.getEnabledLanguages());

        if (languageManager.getEnabledLanguages().isEmpty()) {
            LOGGER.info("No enabled languages found");
            try {
                var res = JOptionPane.showOptionDialog(null,
                        "There are no languages enabled in the IDE, meaning you need to install at least one language's LSP and/or runtime to continue.\nPlease select a language to download the LSP for. This will also open the runtime download page if it's not found,\nas in some cases the runtime is required.",
                        "Download Confirm", 0, JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(ImageIO.read(StartupLogic.class.getResourceAsStream("/icons/popup/save.png"))),
                        languageManager.getAllLanguages().stream().map(Language::getName).toArray(String[]::new), null);

                var allLangs = languageManager.getAllLanguages();
                if (res >= allLangs.size()) {
                    LOGGER.info("Closed prompt, exiting...");
                    System.exit(1);
                }
                var language = allLangs.get(res);
                if (!language.hasRuntime()) {
                    Browse.browse(language.downloadRuntimeLink());
                }

                if (language.installLSP()) {
                    LOGGER.info("Successfully installed LSP. Continuing...");
                } else {
                    LOGGER.error("Could not install LSP. Exiting...");
                    System.exit(1);
                }
            } catch (Exception e) {
                LOGGER.error("Error!", e);
            }
        }

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

                    if (ImageClass.AUTO_TRIM_TEXT) {
                        diagnostics.forEach(diag -> {
                            var range = diag.getRange();
                            var start = range.getStart();
                            var end = range.getEnd();
                            start.setCharacter(imageClass.getLeadingStripped() + start.getCharacter());
                            end.setCharacter(imageClass.getLeadingStripped() + end.getCharacter());
                            range.setStart(start);
                            range.setEnd(end);
                            diag.setRange(range);
                        });
                    }

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
        var optionsFile = new File(MainGUI.APP_DATA, "options.ini");
        var initializeSettings = !optionsFile.exists();
        var settingsManager = SettingsManager.getInstance();
        settingsManager.initialize(optionsFile);
        this.centerPopulator = new CenterPopulator(this);

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

        mainGUI.setIndeterminate(true);

        var imageOutputStream = new ImageOutputStream(this, this.currentLanguage.getAppOutput(), 1500);
        var compilerOutputStream = new ImageOutputStream(this, this.currentLanguage.getCompilerOutput(), 1500);

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

    public List<RPCManager> getRPCManagers() {
        return this.rpcManagers;
    }

    public void runRPC(Consumer<RPCManager> rpcConsumer) {
        this.rpcManagers.forEach(rpcConsumer);
    }
}

package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.languages.ExtraCreationOptions;
import com.uddernetworks.mspaint.code.languages.HighlightData;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.code.lsp.DefaultLanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.LSP;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.util.ExtractUtils;
import com.uddernetworks.mspaint.util.IDEFileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JavaLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(JavaLanguage.class);

    private LanguageSettings settings = new JavaSettings();
    private ExtraCreationOptions extraCreationOptions;
    private JavaCodeManager javaCodeManager = new JavaCodeManager(this);
    private HighlightData highlightData = new JavaHighlightData(this);
    private Map<String, Map<String, String>> replaceData = new HashMap<>();
    private File lspPath = new File(DefaultLanguageServerWrapper.getLSPDirectory(), "java");
    private File templateDir = MainGUI.DEV_MODE ? new File("src\\main\\resources\\lsp\\java\\project-template") : new File(StartupLogic.getJarParent().orElse(new File("")), "lsp\\java\\project-template");
    private LanguageServerWrapper lspWrapper = new DefaultLanguageServerWrapper(this.startupLogic, LSP.JAVA, new File(this.lspPath, "jdt-language-server-latest").getAbsolutePath(),
            Arrays.asList(
                    "java",
                    "-Declipse.application=org.eclipse.jdt.ls.core.id1",
                    "-Dosgi.bundles.defaultStartLevel=4",
                    "-Declipse.product=org.eclipse.jdt.ls.core.product",
                    "-Dlog.level=ALL",
                    "-noverify",
                    "-Xmx1G",
                    "-jar",
                    "%launch-jar%",
                    "-configuration",
                    "%server-path%\\config_win",
                    "-data"
            ), (wrapper, workspaceDir) -> {
        LOGGER.info("Setting up the Java project...");

        if (!new File(workspaceDir.getAbsolutePath(), ".classpath").exists()) {
            LOGGER.info("Copying template files...");

            try {
                FileUtils.copyDirectory(templateDir, workspaceDir);
            } catch (IOException e) {
                LOGGER.error("An error occurred while copying over project template files!", e);
            }
        } else {
            LOGGER.info("Project already contains template files, no need to copy them again.");
        }

        try {
            var corePrefs = new File(workspaceDir, ".settings\\org.eclipse.jdt.core.prefs").toPath();
            var corePrefsTemplate = new File(templateDir, ".settings\\org.eclipse.jdt.core.prefs").toPath();

            var sourceListener = bindFileVariable(corePrefsTemplate, corePrefs, "replace.versionnumber");
            getLanguageSettings().onChangeSetting(JavaLangOptions.JAVA_VERSION, (Consumer<String>) value -> sourceListener.accept(value.substring(5)), true);

            var classpath = new File(workspaceDir, ".classpath").toPath();
            var classpathTemplate = new File(templateDir, ".classpath").toPath();

            var versionListener = bindFileVariable(classpathTemplate, classpath, "replace.version");
            getLanguageSettings().onChangeSetting(JavaLangOptions.JAVA_VERSION, (Consumer<String>) value -> versionListener.accept("JavaSE-" + value.substring(5)), true);

            var srcListener = bindFileVariable(classpathTemplate, classpath, "replace.src");
            getLanguageSettings().onChangeSetting(JavaLangOptions.INPUT_DIRECTORY, (Consumer<File>) file -> srcListener.accept(relativizeFromBase(file)), true);

            var binListener = bindFileVariable(classpathTemplate, classpath, "replace.bin");
            getLanguageSettings().onChangeSetting(JavaLangOptions.CLASS_OUTPUT, (Consumer<File>) file -> binListener.accept(relativizeFromBase(file)), true);

            var project = new File(workspaceDir, ".project").toPath();
            var projectTemplate = new File(templateDir, ".project").toPath();

            LOGGER.info("Replacing to name {}", ProjectManager.getPPFProject().getName());
            bindFileVariable(projectTemplate, project, "replace.name").accept(ProjectManager.getPPFProject().getName());
        } catch (Exception e) { // Caught due to error suppression in the lambdas
            LOGGER.error("There was an exception while writing replacement values for files", e);
        }
    })
            .argumentPreprocessor(((languageServerWrapper, args) -> {
                var serverPath = languageServerWrapper.getServerPath();
                var plugins = new File(serverPath, "plugins");
                var launcherFile = Arrays.stream(plugins.listFiles()).filter(file -> file.getName().startsWith("org.eclipse.equinox.launcher_")).findFirst().orElseThrow(() -> new RuntimeException("Couldn't find launcher jar!"));
                return args.stream().map(str -> str.replace("%server-path%", serverPath).replace("%launch-jar%", launcherFile.getAbsolutePath())).collect(Collectors.toList());
            }));

    public JavaLanguage(StartupLogic startupLogic) {
        super(startupLogic);

        try {
            extraCreationOptions = new JavaCreationExtraOptions(startupLogic.getMainGUI());
        } catch (IOException e) {
            LOGGER.error("Error creating JavaCreationExtraOptions", e);
        }
    }

    private String relativizeFromBase(File file) {
        return ProjectManager.getPPFProject().getFile().getParentFile().toURI().relativize(file.toURI()).toString();
    }

    private Consumer<String> bindFileVariable(Path input, Path output, String variableName) {
        var fileVariables = this.replaceData.computeIfAbsent(input.toString(), i -> new HashMap<>());
        return newValue -> {
            try {
                fileVariables.put(variableName, newValue);
                String[] content = {new String(Files.readAllBytes(input))};
                fileVariables.forEach((variable, value) -> content[0] = content[0].replace("%" + variable + "%", value));
                Files.write(output, content[0].getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("There was a problem writing to " + output.toString(), e);
            }
        };
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void loadForCurrent() {
        var current = ProjectManager.getPPFProject();
        if (current.equals(this.lastInitted)) return;
        this.lastInitted = current;
        LOGGER.info("Build system setting: {}", (Object) settings.getSetting(JavaLangOptions.BUILDSYSTEM));
        getLanguageSettings().initOptions();
    }

    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{"java"};
    }

    @Override
    public Optional<ExtraCreationOptions> getExtraCreationOptions() {
        return Optional.of(extraCreationOptions);
    }

    @Override
    public Option getInputOption() {
        return JavaLangOptions.INPUT_DIRECTORY;
    }

    @Override
    public Option getHighlightOption() {
        return JavaLangOptions.HIGHLIGHT_DIRECTORY;
    }

    @Override
    public File getAppOutput() {
        return getLanguageSettings().getSetting(JavaLangOptions.PROGRAM_OUTPUT);
    }

    @Override
    public File getCompilerOutput() {
        return getLanguageSettings().getSetting(JavaLangOptions.COMPILER_OUTPUT);
    }

    @Override
    public boolean isInterpreted() {
        return false;
    }

    @Override
    public LanguageServerWrapper getLSPWrapper() {
        return this.lspWrapper;
    }

    @Override
    public boolean hasLSP() {
        return new File(this.lspPath, "jdt-language-server-latest").exists();
    }

    @Override
    public boolean installLSP() {
        return lspInstallHelper("Would you like to proceed with downloading the Java Language Server by the Eclipse Foundation? This will take up about 94MB.", "https://github.com/eclipse/eclipse.jdt.ls", () -> {
            LOGGER.info("Installing Java LSP server...");
            this.lspPath.mkdirs();

            var tarGz = new File(this.lspPath, "jdt-language-server-latest.tar.gz");
            FileUtils.copyURLToFile(new URL("http://download.eclipse.org/jdtls/snapshots/jdt-language-server-latest.tar.gz"), tarGz);

            var destDir = new File(this.lspPath, "jdt-language-server-latest");

            try (var in = new TarArchiveInputStream(
                    new GzipCompressorInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(tarGz))))) {
                ExtractUtils.untar(in, destDir);
            }

            tarGz.delete();

            LOGGER.info("Successfully downloaded the Java Language Server");
            return true;
        });
    }

    @Override
    public boolean hasRuntime() {
        return Commandline.runCommand("java", "--version").contains("Runtime Environment");
    }

    @Override
    public String downloadRuntimeLink() {
        return "https://openjdk.java.net/install/";
    }

    @Override
    public HighlightData getHighlightData() {
        return this.highlightData;
    }

    @Override
    public LanguageSettings getLanguageSettings() {
        return this.settings;
    }

    @Override
    public void highlightAll(List<ImageClass> imageClasses) throws IOException {
        if (!this.settings.<Boolean>getSetting(JavaLangOptions.HIGHLIGHT)) return;
        highlightAll(JavaLangOptions.HIGHLIGHT_DIRECTORY, imageClasses);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        if (!getLanguageSettings().<Boolean>getSetting(JavaLangOptions.COMPILE)) new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        var jarFile = this.settings.<File>getSetting(JavaLangOptions.JAR);
        var libDirectoryOptional = this.settings.<File>getSettingOptional(JavaLangOptions.LIBRARY_LOCATION);
        var otherFilesOptional = this.settings.<File>getSettingOptional(JavaLangOptions.OTHER_LOCATION);
        var classOutput = this.settings.<File>getSetting(JavaLangOptions.CLASS_OUTPUT);
        var execute = false;
        if (executeOverride == BuildSettings.EXECUTE) {
            execute = true;
        } else if (executeOverride != BuildSettings.DONT_EXECUTE) {
            execute = this.settings.<Boolean>getSetting(JavaLangOptions.EXECUTE);
        }

        var libFiles = new ArrayList<File>();
        libDirectoryOptional.ifPresent(libDirectory -> {
            if (libDirectory.isFile()) {
                if (libDirectory.getName().endsWith(".jar")) libFiles.add(libDirectory);
            } else {
                libFiles.addAll(IDEFileUtils.getFilesFromDirectory(libDirectory, "jar"));
            }
        });

        return this.javaCodeManager.compileAndExecute(imageClasses, jarFile, otherFilesOptional.orElse(null), classOutput, mainGUI, imageOutputStream, compilerStream, libFiles, execute);
    }
}

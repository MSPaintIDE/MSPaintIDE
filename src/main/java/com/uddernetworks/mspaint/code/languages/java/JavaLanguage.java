package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.code.lsp.LSP;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.util.IDEFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class JavaLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(JavaLanguage.class);

    private JavaSettings settings = new JavaSettings();
    private JavaCodeManager javaCodeManager = new JavaCodeManager();

    public JavaLanguage(StartupLogic startupLogic) {
        super(startupLogic);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
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
    public Option getInputOption() {
        return JavaOptions.INPUT_DIRECTORY;
    }

    @Override
    public File getInputLocation() {
        return getLanguageSettings().getSetting(JavaOptions.INPUT_DIRECTORY);
    }

    @Override
    public File getAppOutput() {
        return getLanguageSettings().getSetting(JavaOptions.PROGRAM_OUTPUT);
    }

    @Override
    public File getCompilerOutput() {
        return getLanguageSettings().getSetting(JavaOptions.COMPILER_OUTPUT);
    }

    @Override
    public boolean isInterpreted() {
        return false;
    }

    @Override
    public LanguageServerWrapper getLSPWrapper() {
        return new LanguageServerWrapper(LSP.JAVA, "E:\\MSPaintIDE\\jdt-language-server-latest",
                Arrays.asList(
                        "java",
                        "-Declipse.application=org.eclipse.jdt.ls.core.id1",
                        "-Dosgi.bundles.defaultStartLevel=4",
                        "-Declipse.product=org.eclipse.jdt.ls.core.product",
                        "-Dlog.level=ALL",
                        "-noverify",
                        "-Xmx1G",
                        "-jar",
                        "%server-path%\\plugins\\org.eclipse.equinox.launcher_1.5.400.v20190515-0925.jar",
                        "-configuration",
                        "%server-path%\\config_win",
                        "-data"
                ));
    }

    @Override
    public boolean hasLSP() {
        return true;
    }

    @Override
    public void installLSP(Consumer<Boolean> successful) {

    }

    @Override
    public boolean hasRuntime() {
        return false;
    }

    @Override
    public String downloadRuntimeLink() {
        return null;
    }

    @Override
    public String getLanguageHighlighter() {
        return null;
    }

    @Override
    public LanguageSettings getLanguageSettings() {
        return this.settings;
    }

    @Override
    public void highlightAll(List<ImageClass> imageClasses) throws IOException {
        if (!this.settings.<Boolean>getSetting(JavaOptions.HIGHLIGHT)) return;
        highlightAll(JavaOptions.HIGHLIGHT_DIRECTORY, imageClasses);
    }

    @Override
    public Optional<List<ImageClass>> indexFiles() {
        return indexFiles(JavaOptions.INPUT_DIRECTORY);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        var imageClassesOptional = indexFiles();
        if (imageClassesOptional.isEmpty()) {
            LOGGER.error("Error while finding ImageClasses, aborting...");
            return new DefaultCompilationResult(Collections.emptyMap(), CompilationResult.Status.COMPILE_COMPLETE);
        }

        return compileAndExecute(mainGUI, imageClassesOptional.get(), imageOutputStream, compilerStream);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        return compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerStream, BuildSettings.DEFAULT);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        var jarFile = this.settings.<File>getSetting(JavaOptions.JAR);
        var libDirectoryOptional = this.settings.<File>getSettingOptional(JavaOptions.LIBRARY_LOCATION);
        var otherFilesOptional = this.settings.<File>getSettingOptional(JavaOptions.OTHER_LOCATION);
        var classOutput = this.settings.<File>getSetting(JavaOptions.CLASS_OUTPUT);
        var execute = false;
        if (executeOverride == BuildSettings.EXECUTE) {
            execute = true;
        } else if (executeOverride != BuildSettings.DONT_EXECUTE) {
            execute = this.settings.<Boolean>getSetting(JavaOptions.EXECUTE);
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

    @Override
    public String toString() {
        return getName();
    }
}

package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.OverrideExecute;
import com.uddernetworks.mspaint.code.languages.DefaultJFlexLexer;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageError;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.util.IDEFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        return new String[] {"java"};
    }

    @Override
    public String getOutputFileExtension() {
        return "jar";
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
    public boolean meetsRequirements() {
        return ToolProvider.getSystemJavaCompiler() != null;
    }

    @Override
    public DefaultJFlexLexer getLanguageHighlighter() {
        return new JavaLexer();
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
    public Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        var imageClassesOptional = indexFiles();
        if (imageClassesOptional.isEmpty()) {
            LOGGER.error("Error while finding ImageClasses, aborting...");
            return Collections.emptyMap();
        }

        return compileAndExecute(mainGUI, imageClassesOptional.get(), imageOutputStream, compilerStream);
    }

    @Override
    public Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        return compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerStream, OverrideExecute.DEFAULT);
    }

    @Override
    public Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, OverrideExecute executeOverride) throws IOException {
        var jarFile = this.settings.<File>getSetting(JavaOptions.JAR);
        var libDirectoryOptional = this.settings.<File>getSettingOptional(JavaOptions.LIBRARY_LOCATION);
        var otherFilesOptional = this.settings.<File>getSettingOptional(JavaOptions.OTHER_LOCATION);
        var classOutput = this.settings.<File>getSetting(JavaOptions.CLASS_OUTPUT);
        var execute = switch (executeOverride) {
            case EXECUTE -> true;
            case DONT_EXECUTE -> false;
            default -> this.settings.<Boolean>getSetting(JavaOptions.EXECUTE);
        };

        var libFiles = new ArrayList<File>();
        libDirectoryOptional.ifPresent(libDirectory -> {
            if (libDirectory.isFile()) {
                if (libDirectory.getName().endsWith(".jar")) libFiles.add(libDirectory);
            } else {
                libFiles.addAll(IDEFileUtils.getFilesFromDirectory(libDirectory, "jar"));
            }
        });

        return this.javaCodeManager.compileAndExecute(imageClasses, jarFile, otherFilesOptional.orElse(null), classOutput, mainGUI, imageOutputStream, compilerStream, libFiles, execute)
                .entrySet()
                .stream()
                .map(t -> new AbstractMap.SimpleEntry<ImageClass, List<LanguageError>>(t.getKey(), t.getValue().stream().map(JavaError::new).collect(Collectors.toList())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public String toString() {
        return getName();
    }
}

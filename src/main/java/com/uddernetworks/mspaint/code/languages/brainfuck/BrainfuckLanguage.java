//package com.uddernetworks.mspaint.code.languages.brainfuck;
//
//import com.uddernetworks.mspaint.code.BuildSettings;
//import com.uddernetworks.mspaint.code.ImageClass;
//import com.uddernetworks.mspaint.code.execution.CompilationResult;
//import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
//import com.uddernetworks.mspaint.code.languages.Language;
//import com.uddernetworks.mspaint.code.languages.LanguageSettings;
//import com.uddernetworks.mspaint.code.languages.java.JavaLanguage;
//import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
//import com.uddernetworks.mspaint.main.MainGUI;
//import com.uddernetworks.mspaint.main.StartupLogic;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//public class BrainfuckLanguage extends Language {
//
//    private static Logger LOGGER = LoggerFactory.getLogger(JavaLanguage.class);
//
//    private BrainfuckSettings settings = new BrainfuckSettings();
//    private BrainfuckCodeManager codeManager = new BrainfuckCodeManager();
//
//    public BrainfuckLanguage(StartupLogic startupLogic) {
//        super(startupLogic);
//    }
//
//    @Override
//    public Logger getLogger() {
//        return LOGGER;
//    }
//
//    @Override
//    public String getName() {
//        return "Brainfuck";
//    }
//
//    @Override
//    public String[] getFileExtensions() {
//        return new String[]{"brainfuck", "bf", "b"};
//    }
//
//    @Override
//    public String getOutputFileExtension() {
//        return null;
//    }
//
//    @Override
//    public File getInputLocation() {
//        return getLanguageSettings().getSetting(BrainfuckOptions.INPUT_DIRECTORY);
//    }
//
//    @Override
//    public File getAppOutput() {
//        return getLanguageSettings().getSetting(BrainfuckOptions.PROGRAM_OUTPUT);
//    }
//
//    @Override
//    public File getCompilerOutput() {
//        return getLanguageSettings().getSetting(BrainfuckOptions.COMPILER_OUTPUT);
//    }
//
//    @Override
//    public boolean isInterpreted() {
//        return true;
//    }
//
//    @Override
//    public boolean meetsRequirements() {
//        return true;
//    }
//
//    @Override
//    public DefaultJFlexLexer getLanguageHighlighter() {
//        return new BrainfuckLexer();
//    }
//
//    @Override
//    public LanguageSettings getLanguageSettings() {
//        return this.settings;
//    }
//
//    @Override
//    public void highlightAll(List<ImageClass> imageClasses) throws IOException {
//        if (!this.settings.<Boolean>getSetting(BrainfuckOptions.HIGHLIGHT)) return;
//        highlightAll(BrainfuckOptions.HIGHLIGHT_DIRECTORY, imageClasses);
//    }
//
//    @Override
//    public Optional<List<ImageClass>> indexFiles() {
//        return Optional.empty();
//    }
//
//    @Override
//    public CompilationResult compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) {
//        var imageClassesOptional = indexFiles();
//        if (imageClassesOptional.isEmpty()) {
//            LOGGER.error("Error while finding ImageClasses, aborting...");
//            return new DefaultCompilationResult(Collections.emptyMap(), CompilationResult.Status.COMPILE_COMPLETE);
//        }
//
//        return compileAndExecute(mainGUI, imageClassesOptional.get(), imageOutputStream, compilerStream);
//    }
//
//    @Override
//    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) {
//        return compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerStream, BuildSettings.DEFAULT);
//    }
//
//    @Override
//    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) {
//        return this.codeManager.executeCode(mainGUI, imageClasses, imageOutputStream, compilerStream);
//    }
//
//    @Override
//    public String toString() {
//        return getName();
//    }
//
//}

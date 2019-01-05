package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageError;
import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaLanguage implements Language {

    private CodeCompiler codeCompiler = new CodeCompiler();
    private LanguageHighlighter languageHighlighter = new JavaLanguageHighlighter();

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
    public boolean isInterpreted() {
        return false;
    }

    @Override
    public boolean meetsRequirements() {
        return ToolProvider.getSystemJavaCompiler() != null;
    }

    @Override
    public LanguageHighlighter getLanguageHighlighter() {
        return this.languageHighlighter;
    }

    @Override
    public Map<ImageClass, List<LanguageError>> compileAndExecute(List<ImageClass> imageClasses, File outputFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        return this.codeCompiler.compileAndExecute(imageClasses, outputFile, otherFiles, classOutputFolder, mainGUI, imageOutputStream, compilerStream, libs, execute)
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

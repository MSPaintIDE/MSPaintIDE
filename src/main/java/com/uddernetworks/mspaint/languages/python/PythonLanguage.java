package com.uddernetworks.mspaint.languages.python;

import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.languages.Language;
import com.uddernetworks.mspaint.languages.LanguageError;
import com.uddernetworks.mspaint.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.main.ImageClass;
import com.uddernetworks.mspaint.main.MainGUI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PythonLanguage implements Language {

    private PythonLanguageHighlighter pythonLanguageHighlighter = new PythonLanguageHighlighter();

    @Override
    public String getName() {
        return "Python";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{"py", "py3", "pyw"};
    }

    @Override
    public String getOutputFileExtension() {
        return null;
    }

    @Override
    public boolean isInterpreted() {
        return true;
    }

    @Override
    public boolean meetsRequirements() {
        return true;
    }

    @Override
    public LanguageHighlighter getLanguageHighlighter() {
        return this.pythonLanguageHighlighter;
    }

    @Override
    public Map<ImageClass, List<LanguageError>> compileAndExecute(List<ImageClass> imageClasses, File outputFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        Map<ImageClass, List<LanguageError>> errors = new HashMap<>();

//        PrintStream imageOut = new PrintStream(imageOutputStream);
//        PrintStream compilerOut = new PrintStream(compilerStream);
//        compilerStream.changeColor(Color.RED);
//
//        for (ImageClass imageClass : imageClasses) {
//            long start = System.currentTimeMillis();
//
//            PrintStream oldPS = System.out;
//            System.setOut(imageOut);
//
//            String code = imageClass.getText();
//
//            compilerOut.println("Checking code...");
//            mainGUI.setStatusText("Checking code...");
//
//            MultiLineBlock multiLineBlock = new MultiLineBlock(code);
//
//            String error = check(multiLineBlock);
//
//            if (error != null) {
//                errors.put(imageClass, Arrays.asList(new PythonError(multiLineBlock.getLine() + 1, multiLineBlock.getCharacter() + 1, imageClass.getInputImage().getName(), error)));
//
//                compilerOut.println("Error on " + imageClass.getInputImage().getName() + " [" + multiLineBlock.getLine() + ":" + multiLineBlock.getCharacter() + "] " + error);
//
//                compilerOut.println("Completed in " + (System.currentTimeMillis() - start));
//
//                System.setOut(oldPS);
//                return errors;
//            }
//
//            compilerOut.println("Executing...");
//            mainGUI.setStatusText("Executing...");
//
//            start = System.currentTimeMillis();
//
//            execute(code);
//
//            System.setOut(oldPS);
//
//            compilerOut.println("Executed in " + (System.currentTimeMillis() - start) + "ms");
//            mainGUI.setStatusText("");
//        }

        return errors;
    }

    @Override
    public String toString() {
        return getName();
    }
}

package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.highlighter.AngrySquiggleHighlighter;
import com.uddernetworks.mspaint.highlighter.CustomJavaRenderer;
import com.uddernetworks.mspaint.highlighter.LetterFormatter;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.mspaint.ocr.ImageIndex;
import com.uddernetworks.mspaint.ocr.LetterGrid;
import javafx.scene.control.Alert;

import javax.swing.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {

    private Map<String, BufferedImage> images;

    private File inputImage = null;
    private File highlightedFile = null;
    private File objectFile = null;
    private File classOutput = null;
    private File compilerOutput = null;
    private File appOutput = null;
    private File letterDirectory = null;

    private String text;
    private LetterFileWriter letterFileWriter;
    private List<List<Letter>> letterGrid;
    private File parent;
    private File currentJar;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            Main main = new Main();
            main.start();
        } catch (IOException | InstantiationException | IllegalAccessException | URISyntaxException | ClassNotFoundException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException, URISyntaxException {
        currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        parent = currentJar.getParentFile();

        parseOptions();

        MainWindow mainWindow = new MainWindow();
        mainWindow.display();
        mainWindow.registerThings(this, currentJar);
        JTextPane textArea = mainWindow.getTextAreaOutput();

        TextPrintStream textPrintStream = new TextPrintStream(textArea);
        PrintStream textOut = new PrintStream(textPrintStream);
        System.setOut(textOut);
        System.setErr(textOut);
    }

    private void parseOptions() throws IOException {
        List<String> options = Files.readAllLines(Paths.get(getOptions().getAbsolutePath()));

        for (String option : options) {
            String[] split = option.split(" ");
            String firstPart = split[0];
            String secondPart = String.join(" ", Arrays.copyOfRange(split, 1, split.length));

            switch (firstPart) {
                case "inputImage":
                    inputImage = new File(secondPart);
                    break;
                case "highlightedFile":
                    highlightedFile = new File(secondPart);
                    break;
                case "objectFile":
                    objectFile = new File(secondPart);
                    break;
                case "classOutput":
                    classOutput = new File(secondPart);
                    break;
                case "compilerOutput":
                    compilerOutput = new File(secondPart);
                    break;
                case "appOutput":
                    appOutput = new File(secondPart);
                    break;
                case "letterDirectory":
                    letterDirectory = new File(secondPart);
                    break;
            }
        }
    }

    private void saveOptions() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("inputImage ").append(getInputImage()).append("\n");
        stringBuilder.append("highlightedFile ").append(getHighlightedFile()).append("\n");
        stringBuilder.append("objectFile ").append(getObjectFile()).append("\n");
        stringBuilder.append("classOutput ").append(getClassOutput()).append("\n");
        stringBuilder.append("compilerOutput ").append(getCompilerOutput()).append("\n");
        stringBuilder.append("appOutput ").append(getAppOutput()).append("\n");
        stringBuilder.append("letterDirectory ").append(getLetterDirectory());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getOptions()))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getOptions() throws IOException {
        File optionsTxt = new File(parent.getAbsolutePath(), "options.txt");
        if (!optionsTxt.exists()) optionsTxt.createNewFile();
        return optionsTxt;
    }

    private boolean optionsNotFilled() {
        return inputImage == null || highlightedFile == null || objectFile == null || classOutput == null || compilerOutput == null || appOutput == null || letterDirectory == null;
    }

    public long highlight(boolean printOutTime) throws IOException {
        if (optionsNotFilled()) {
            JOptionPane.showMessageDialog(null, "Please select files for all options", "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        final long originalStart = System.currentTimeMillis();

        System.out.println("Scanning image...");
        long start = System.currentTimeMillis();

        ImageIndex imageIndex = new ImageIndex(letterDirectory);
        images = imageIndex.index();

        ImageCompare imageCompare = new ImageCompare();

        ModifiedDetector modifiedDetector = new ModifiedDetector(inputImage, objectFile);

        LetterGrid grid = imageCompare.getText(inputImage, objectFile, images, !modifiedDetector.imageChanged());

        letterGrid = grid.getLetterGridArray();
        text = grid.getPrettyString();

        System.out.println("\n\ntext =\n" + text);

        System.out.println("Finished scan in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("\nHighlighting...");
        start = System.currentTimeMillis();

        CustomJavaRenderer renderer = new CustomJavaRenderer();
        String highlighted = renderer.highlight(text);

        System.out.println("Finished highlighting in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Modifying letters...");
        start = System.currentTimeMillis();

        LetterFormatter letterFormatter = new LetterFormatter(letterGrid);
        letterFormatter.formatLetters(highlighted);

        System.out.println("Finished modifying letters in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Writing highlighted image to file...");
        start = System.currentTimeMillis();


        letterFileWriter = new LetterFileWriter(letterGrid, inputImage, highlightedFile);
        letterFileWriter.writeToFile(images);

        System.out.println("Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");

        if (printOutTime) {
            System.out.println("Finished everything in " + (System.currentTimeMillis() - originalStart) + "ms");
        }

        return originalStart;
    }

    public void compile() throws IOException {
        final long originalStart = highlight(false);

        if (originalStart == -1) return;

        long start = System.currentTimeMillis();

        BufferedImage image = letterFileWriter.getImage();

        System.out.println("Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Executing...");
        start = System.currentTimeMillis();


        CodeCompiler codeCompiler = new CodeCompiler();

        ImageOutputStream imageOutputStream = new ImageOutputStream(appOutput, 500);
        ImageOutputStream compilerOutputStream = new ImageOutputStream(compilerOutput, 500);
        List<Diagnostic<? extends JavaFileObject>> errors = codeCompiler.compileAndExecute(text, classOutput, imageOutputStream, compilerOutputStream);

        AngrySquiggleHighlighter highlighter = new AngrySquiggleHighlighter(image, 3, new File(letterDirectory.getAbsoluteFile(),"angry_squiggle.png"), highlightedFile, letterGrid, errors);
        highlighter.highlightAngrySquiggles();

        System.out.println("Finished executing in " + (System.currentTimeMillis() - start) + "ms");

        imageOutputStream.saveImage();
        compilerOutputStream.saveImage();

        System.out.println("Finished everything in " + (System.currentTimeMillis() - originalStart) + "ms");
    }

    public void setInputImage(File inputImage) {
        this.inputImage = inputImage;
        saveOptions();
    }

    public void setHighlightedFile(File highlightedFile) {
        this.highlightedFile = highlightedFile;
        saveOptions();
    }

    public void setObjectFile(File objectFile) {
        this.objectFile = objectFile;
        saveOptions();
    }

    public void setClassOutput(File classOutput) {
        this.classOutput = classOutput;
        saveOptions();
    }

    public void setCompilerOutput(File compilerOutput) {
        this.compilerOutput = compilerOutput;
        saveOptions();
    }

    public void setAppOutput(File appOutput) {
        this.appOutput = appOutput;
        saveOptions();
    }

    public void setLetterDirectory(File letterDirectory) {
        this.letterDirectory = letterDirectory;
        saveOptions();
    }

    public String getInputImage() {
        return (inputImage == null) ? "" : inputImage.getAbsolutePath();
    }

    public String getHighlightedFile() {
        return (highlightedFile == null) ? "" : highlightedFile.getAbsolutePath();
    }

    public String getObjectFile() {
        return (objectFile == null) ? "" : objectFile.getAbsolutePath();
    }

    public String getClassOutput() {
        return (classOutput == null) ? "" : classOutput.getAbsolutePath();
    }

    public String getCompilerOutput() {
        return (compilerOutput == null) ? "" : compilerOutput.getAbsolutePath();
    }

    public String getAppOutput() {
        return (appOutput == null) ? "" : appOutput.getAbsolutePath();
    }

    public String getLetterDirectory() {
        return (letterDirectory == null) ? "" : letterDirectory.getAbsolutePath();
    }
}

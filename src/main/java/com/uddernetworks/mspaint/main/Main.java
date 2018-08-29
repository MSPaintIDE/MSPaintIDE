package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.highlighter.AngrySquiggleHighlighter;
import com.uddernetworks.mspaint.ocr.ImageIndex;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    private Map<String, BufferedImage> images;

    public File inputImage = null;
    private File highlightedFile = null;
    private File objectFile = null;
    private File classOutput = null;
    private File jarFile = null;
    private File libraryFile = null;
    private File otherFiles = null;
    private File compilerOutput = null;
    private File appOutput = null;
    private File letterDirectory = null;

    private String text;
    private LetterFileWriter letterFileWriter;
    private List<List<Letter>> letterGrid;
    private File parent;
    private File currentJar;

    private MainGUI mainGUI;

    private List<ImageClass> imageClasses = new ArrayList<>();

    public void start(MainGUI mainGUI) throws IOException, URISyntaxException {
        this.mainGUI = mainGUI;
        currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        parent = currentJar.getParentFile();

        parseOptions();
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
                case "jarFile":
                    jarFile = new File(secondPart);
                    break;
                case "libraryFile":
                    libraryFile = "".equals(secondPart) ? null : new File(secondPart);
                    break;
                case "otherFiles":
                    otherFiles = "".equals(secondPart) ? null : new File(secondPart);
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
        stringBuilder.append("jarFile ").append(getJarFile()).append("\n");
        stringBuilder.append("libraryFile ").append(getLibraryFile()).append("\n");
        stringBuilder.append("otherFiles ").append(getOtherFiles()).append("\n");
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

    public int indexAll(boolean useProbe, boolean useCaches, boolean saveCaches) {
        if (optionsNotFilled()) {
            System.err.println("Please select files for all options");
            mainGUI.setHaveError();
            return -1;
        }

        System.out.println("Scanning all images...");
        long start = System.currentTimeMillis();

        mainGUI.setStatusText("Indexing letters...");

        ImageIndex imageIndex = new ImageIndex(letterDirectory);
        images = imageIndex.index();

        mainGUI.setStatusText(null);

        if (inputImage.isDirectory()) {
            for (File imageFile : getFilesFromDirectory(inputImage, "png")) {
                imageClasses.add(new ImageClass(imageFile, objectFile, mainGUI, images, useProbe, useCaches, saveCaches));
            }
        } else {
            imageClasses.add(new ImageClass(inputImage, objectFile, mainGUI, images, useProbe, useCaches, saveCaches));
        }

        mainGUI.setStatusText(null);

        System.out.println("Finished scanning all images in " + (System.currentTimeMillis() - start) + "ms");
        return 1;
    }

    public void highlightAll() throws IOException {
        if (optionsNotFilled()) {
            System.err.println("Please select files for all options");
            mainGUI.setHaveError();
            return;
        }

        System.out.println("Scanning all images...");
        mainGUI.setStatusText("Highlighting...");
        mainGUI.setIndeterminate(true);
        long start = System.currentTimeMillis();

        for (ImageClass imageClass : imageClasses) {
            imageClass.highlight(highlightedFile);
        }

        mainGUI.setIndeterminate(false);
        mainGUI.setStatusText(null);

        System.out.println("Finished highlighting all images in " + (System.currentTimeMillis() - start) + "ms");
    }



    public void compile(boolean execute) throws IOException {
        long start = System.currentTimeMillis();
        final long originalStart = start;

        System.out.println("Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Compiling...");
        mainGUI.setStatusText("Compiling...");
        mainGUI.setIndeterminate(true);
        start = System.currentTimeMillis();


        CodeCompiler codeCompiler = new CodeCompiler();

        List<File> libFiles = new ArrayList<>();
        if (libraryFile != null) {
            if (libraryFile.isFile()) {
                if (libraryFile.getName().endsWith(".jar")) {
                    libFiles.add(libraryFile);
                }
            } else {
                libFiles.addAll(getFilesFromDirectory(libraryFile, "jar"));
            }
        }

        ImageOutputStream imageOutputStream = new ImageOutputStream(appOutput, 500);
        ImageOutputStream compilerOutputStream = new ImageOutputStream(compilerOutput, 500);
        Map<ImageClass, List<Diagnostic<? extends JavaFileObject>>> errors = codeCompiler.compileAndExecute(imageClasses, jarFile, otherFiles, classOutput, mainGUI, imageOutputStream, compilerOutputStream, libFiles, execute);

        System.out.println("Highlighting Angry Squiggles...");
        mainGUI.setStatusText("Highlighting Angry Squiggles...");

        for (ImageClass imageClass : errors.keySet()) {
            AngrySquiggleHighlighter highlighter = new AngrySquiggleHighlighter(imageClass.getImage(), 3, new File(letterDirectory.getAbsoluteFile(), "angry_squiggle.png"), imageClass.getHighlightedFile(), imageClass.getLetterGrid(), errors.get(imageClass));
            highlighter.highlightAngrySquiggles();
        }


        System.out.println("Finished compiling in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Saving output images...");
        mainGUI.setStatusText("Saving output images...");

        imageOutputStream.saveImage();
        compilerOutputStream.saveImage();

        mainGUI.setStatusText(null);

        System.out.println("Finished everything in " + (System.currentTimeMillis() - originalStart) + "ms");
    }

    public List<File> getFilesFromDirectory(File directory, String extension) {
        List<File> ret = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ret.addAll(getFilesFromDirectory(file, extension));
            } else {
                if (file.getName().endsWith("." + extension)) ret.add(file);
            }
        }

        return ret;
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

    public void setJarFile(File jarFile) {
        this.jarFile = jarFile;
        saveOptions();
    }

    public void setLibraryFile(File libraryFile) {
        this.libraryFile = libraryFile;
        saveOptions();
    }

    public void setOtherFiles(File libraryFile) {
        this.otherFiles = libraryFile;
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

    public String getJarFile() {
        return (jarFile == null) ? "" : jarFile.getAbsolutePath();
    }

    public String getLibraryFile() {
        return (libraryFile == null) ? "" : libraryFile.getAbsolutePath();
    }

    public String getOtherFiles() {
        return (otherFiles == null) ? "" : otherFiles.getAbsolutePath();
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

    public File getCurrentJar() {
        return currentJar;
    }
}

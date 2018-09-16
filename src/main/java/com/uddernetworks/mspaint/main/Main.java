package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.highlighter.AngrySquiggleHighlighter;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.ocr.ImageIndex;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        Properties properties = new Properties();
        properties.load(Files.newInputStream(getOptions().toPath()));

        inputImage = getProperty(properties, "inputImage");
        highlightedFile = getProperty(properties, "highlightedFile");
        objectFile = getProperty(properties, "objectFile");
        classOutput = getProperty(properties, "classOutput");
        jarFile = getProperty(properties, "jarFile");
        libraryFile = getProperty(properties, "libraryFile");
        otherFiles = getProperty(properties, "otherFiles");
        compilerOutput = getProperty(properties, "compilerOutput");
        appOutput = getProperty(properties, "appOutput");
        letterDirectory = getProperty(properties, "letterDirectory");
    }

    private File getProperty(Properties properties, String property) {
        String propertyText = properties.getProperty(property, null);
        return propertyText == null || propertyText.equals("") ? null : new File(propertyText);
    }

    private void saveOptions() {
        Properties properties = new Properties();

        properties.setProperty("inputImage", getInputImage());
        properties.setProperty("highlightedFile", getHighlightedFile());
        properties.setProperty("objectFile", getObjectFile());
        properties.setProperty("classOutput", getClassOutput());
        properties.setProperty("jarFile", getJarFile());
        properties.setProperty("libraryFile", getLibraryFile());
        properties.setProperty("otherFiles", getOtherFiles());
        properties.setProperty("compilerOutput", getCompilerOutput());
        properties.setProperty("appOutput", getAppOutput());
        properties.setProperty("letterDirectory", getLetterDirectory());

        try {
            OutputStream outputStream = new FileOutputStream(getOptions());
            properties.store(outputStream, "MS Paint IDE Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getOptions() throws IOException {
        File optionsIni = new File(parent.getAbsolutePath(), "options.ini");
        if (!optionsIni.exists()) optionsIni.createNewFile();
        return optionsIni;
    }

    private boolean optionsNotFilled() {
        return inputImage == null || classOutput == null || compilerOutput == null || appOutput == null || letterDirectory == null;
    }

    public int indexAll(boolean useProbe, boolean useCaches, boolean saveCaches) {
        if (this.letterDirectory == null) {
            File localMSPaintIDE = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");
            if (localMSPaintIDE.exists()) {
                this.letterDirectory = new File(localMSPaintIDE, "letters");
            }
        }

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

        if (highlightedFile != null && !highlightedFile.isDirectory()) highlightedFile.mkdirs();

        if (highlightedFile == null || !highlightedFile.isDirectory()) {
            System.err.println("No highlighted file directory found!");
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

        System.out.println("Compiling...");
        mainGUI.setStatusText("Compiling...");
        mainGUI.setIndeterminate(true);

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

        System.out.println("Saving output images...");
        mainGUI.setStatusText("Saving output images...");

        imageOutputStream.saveImage();
        compilerOutputStream.saveImage();

        mainGUI.setStatusText(null);

        System.out.println("Finished compiling in " + (System.currentTimeMillis() - start) + "ms");

        imageClasses.clear();
    }

    public List<File> getFilesFromDirectory(File directory, String extension) {
        List<File> ret = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ret.addAll(getFilesFromDirectory(file, extension));
            } else {
                if (extension == null || file.getName().endsWith("." + extension)) ret.add(file);
            }
        }

        return ret;
    }

    public void setInputImage(File inputImage) {
        if (inputImage.equals(this.inputImage)) return;
        this.inputImage = inputImage;
        saveOptions();

        File outputParent = inputImage.getParentFile();

        if (highlightedFile == null) {
            setHighlightedFile(new File(outputParent, "highlighted"));
        }

        if (compilerOutput == null) {
            setCompilerOutput(new File(outputParent, "compiler.png"));
        }

        if (appOutput == null) {
            setAppOutput(new File(outputParent, "program.png"));
        }

        if (jarFile == null) {
            setJarFile(new File(outputParent, "Output.jar"));
        }

        if (classOutput == null) {
            setClassOutput(new File(outputParent, "classes"));
        }

        this.mainGUI.initializeInputTextFields();
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
        if (letterDirectory == null) {
            File localMSPaintIDE = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE");
            if (!localMSPaintIDE.exists()) return "";

            File directory = new File(localMSPaintIDE, "letters");
            return (directory.exists() && directory.isDirectory()) ? directory.getAbsolutePath() : "";
        }

        return letterDirectory.getAbsolutePath();
    }

    public File getCurrentJar() {
        return currentJar;
    }
}

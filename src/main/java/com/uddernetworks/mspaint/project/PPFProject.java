package com.uddernetworks.mspaint.project;

import java.io.File;

public class PPFProject {

    private File file;

    @PPFSetting
    private File inputLocation;         // Input file/image folder

    @PPFSetting
    private File highlightLocation;     // Highlight output file/folder

    @PPFSetting
    private File objectLocation;        // Folder for cached files

    @PPFSetting
    private File classLocation;         // Output .class file folder

    @PPFSetting
    private File jarFile;               // Output .jar file

    @PPFSetting
    private File libraryLocation;       // Library .jar/folder

    @PPFSetting
    private File otherLocation;         // File/folder for other packaged files

    @PPFSetting
    private File compilerOutput;        // Compiler output image file

    @PPFSetting
    private File appOutput;             // Application output image file

    @PPFSetting
    private String language;            // The language used in the project

    @PPFSetting
    private String name;                // The name of the project

    @PPFSetting
    private boolean syntaxHighlight;    // If the IDE should highlight the code

    @PPFSetting
    private boolean compile;            // If the IDE should compile the code

    @PPFSetting
    private boolean execute;            // If the IDE should execute the code

    @PPFSetting
    private boolean useCaches;          // If caches should be used

    @PPFSetting
    private boolean saveCaches;         // If caches should be saved

    public PPFProject(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }



    public File getInputLocation() {
        return inputLocation;
    }

    public void setInputLocation(File inputLocation) {
        setInputLocation(inputLocation, true);
    }

    public void setInputLocation(File inputLocation, boolean override) {
        if (this.inputLocation == null || override) this.inputLocation = inputLocation;
    }


    public File getHighlightLocation() {
        return highlightLocation;
    }

    public void setHighlightLocation(File highlightLocation) {
        setHighlightLocation(highlightLocation, true);
    }

    public void setHighlightLocation(File highlightLocation, boolean override) {
        if (this.highlightLocation == null || override) this.highlightLocation = highlightLocation;
    }


    public File getObjectLocation() {
        return objectLocation;
    }

    public void setObjectLocation(File objectLocation) {
        setObjectLocation(objectLocation, true);
    }

    public void setObjectLocation(File objectLocation, boolean override) {
        if (this.objectLocation == null || override) this.objectLocation = objectLocation;
    }


    public File getClassLocation() {
        return classLocation;
    }

    public void setClassLocation(File classLocation) {
        this.classLocation = classLocation;
    }

    public void setClassLocation(File classLocation, boolean override) {
        if (this.classLocation == null || override) this.classLocation = classLocation;
    }


    public File getJarFile() {
        return jarFile;
    }

    public void setJarFile(File jarFile) {
        setJarFile(jarFile, true);
    }

    public void setJarFile(File jarFile, boolean override) {
        if (this.jarFile == null || override) this.jarFile = jarFile;
    }


    public File getLibraryLocation() {
        return libraryLocation;
    }

    public void setLibraryLocation(File libraryLocation) {
        setLibraryLocation(libraryLocation, true);
    }

    public void setLibraryLocation(File libraryLocation, boolean override) {
        if (this.libraryLocation == null || override) this.libraryLocation = libraryLocation;
    }


    public File getOtherLocation() {
        return otherLocation;
    }

    public void setOtherLocation(File otherLocation) {
        setOtherLocation(otherLocation, true);
    }

    public void setOtherLocation(File otherLocation, boolean override) {
        if (this.otherLocation == null || override) this.otherLocation = otherLocation;
    }


    public File getCompilerOutput() {
        return compilerOutput;
    }

    public void setCompilerOutput(File compilerOutput) {
        setCompilerOutput(compilerOutput, true);
    }

    public void setCompilerOutput(File compilerOutput, boolean override) {
        if (this.compilerOutput == null || override) this.compilerOutput = compilerOutput;
    }


    public File getAppOutput() {
        return appOutput;
    }

    public void setAppOutput(File appOutput) {
        setAppOutput(appOutput, true);
    }

    public void setAppOutput(File appOutput, boolean override) {
        if (this.appOutput == null || override) this.appOutput = appOutput;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        setLanguage(language, true);
    }

    public void setLanguage(String language, boolean override) {
        if (this.language == null || override) this.language = language;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        setName(name, true);
    }

    public void setName(String name, boolean override) {
        if (this.name == null || override) this.name = name;
    }


    public boolean isSyntaxHighlight() {
        return syntaxHighlight;
    }

    public void setSyntaxHighlight(boolean syntaxHighlight) {
        this.syntaxHighlight = syntaxHighlight;
    }


    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }


    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

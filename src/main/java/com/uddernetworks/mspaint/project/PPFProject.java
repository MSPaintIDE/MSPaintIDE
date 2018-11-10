package com.uddernetworks.mspaint.project;

import java.io.File;

public class PPFProject {

    private File inputLocation;         // Input file/image folder
    private File highlightLocation;     // Highlight output file/folder
    private File classLocation;         // Output .class file folder
    private File jarFile;               // Output .jar file
    private File libraryLocation;       // Library .jar/folder
    private File otherLocation;         // File/folder for other packaged files
    private File compilerOutput;        // Compiler output image file
    private File appOutput;             // Application output image file

    public PPFProject() {

    }

    public File getInputLocation() {
        return inputLocation;
    }

    public void setInputLocation(File inputLocation) {
        this.inputLocation = inputLocation;
    }

    public File getHighlightLocation() {
        return highlightLocation;
    }

    public void setHighlightLocation(File highlightLocation) {
        this.highlightLocation = highlightLocation;
    }

    public File getClassLocation() {
        return classLocation;
    }

    public void setClassLocation(File classLocation) {
        this.classLocation = classLocation;
    }

    public File getJarFile() {
        return jarFile;
    }

    public void setJarFile(File jarFile) {
        this.jarFile = jarFile;
    }

    public File getLibraryLocation() {
        return libraryLocation;
    }

    public void setLibraryLocation(File libraryLocation) {
        this.libraryLocation = libraryLocation;
    }

    public File getOtherLocation() {
        return otherLocation;
    }

    public void setOtherLocation(File otherLocation) {
        this.otherLocation = otherLocation;
    }

    public File getCompilerOutput() {
        return compilerOutput;
    }

    public void setCompilerOutput(File compilerOutput) {
        this.compilerOutput = compilerOutput;
    }

    public File getAppOutput() {
        return appOutput;
    }

    public void setAppOutput(File appOutput) {
        this.appOutput = appOutput;
    }
}

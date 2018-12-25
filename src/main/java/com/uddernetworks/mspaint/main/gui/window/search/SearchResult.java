package com.uddernetworks.mspaint.main.gui.window.search;

import com.uddernetworks.newocr.ScannedImage;
import com.uddernetworks.newocr.character.ImageLetter;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResult {

    private final File file;
    private final ScannedImage scannedImage;
    private String text;
    private boolean ignoreCase;
    private final List<ImageLetter> imageLetters;
    private List<ImageLetter> contextLine;
    private int foundPosition;
    private int lineNumber;

    public SearchResult(File file, ScannedImage scannedImage, String text, boolean ignoreCase, List<ImageLetter> imageLetters, List<ImageLetter> contextLine, int foundPosition, int lineNumber) {
        this.file = file;
        this.scannedImage = scannedImage;
        this.text = text;
        this.ignoreCase = ignoreCase;
        this.imageLetters = imageLetters;
        this.contextLine = contextLine;
        this.foundPosition = foundPosition;
        this.lineNumber = lineNumber;
    }

    public File getFile() {
        return file;
    }

    public ScannedImage getScannedImage() {
        return scannedImage;
    }

    public String getText() {
        return text;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public List<ImageLetter> getImageLetters() {
        return imageLetters;
    }

    public List<ImageLetter> getContextLine() {
        return contextLine;
    }

    public int getFoundPosition() {
        return foundPosition;
    }

    public String getFullLine() {
        return imageLettersToString(this.contextLine);
    }

    @Override
    public String toString() {
        int startLeft = Math.max(this.foundPosition - 10, 0);
        int goToRight = Math.min(this.foundPosition + 10, this.contextLine.size());

        List<ImageLetter> displayContextLine = this.contextLine.subList(startLeft, goToRight);
        return (startLeft != 0 ? "..." : "") +
                displayContextLine.stream()
                .map(ImageLetter::getLetter)
                .map(String::valueOf)
                .collect(Collectors.joining("")) + (goToRight != this.contextLine.size() ? "..." : "");
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public static String imageLettersToString(List<ImageLetter> imageLetters) {
        return imageLetters.stream()
                .map(ImageLetter::getLetter)
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }
}

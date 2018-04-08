package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.highlighter.CustomJavaRenderer;
import com.uddernetworks.mspaint.highlighter.LetterFormatter;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.mspaint.ocr.LetterGrid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ImageClass {

    private File inputImage;
    private List<List<Letter>> letterGrid;
    private String text;
    private LetterFileWriter letterFileWriter;
    private Map<String, BufferedImage> images;
    private File highlightedFile;
    private Test test;

    public ImageClass(File inputImage, File objectFileDir, Test test, Map<String, BufferedImage> images, boolean useProbe, boolean useCaches, boolean saveCaches) {
        this.inputImage = inputImage;
        this.test = test;
        this.images = images;

        File objectFile = new File(objectFileDir, inputImage.getName().substring(0, inputImage.getName().length() - 4) + "_cache.txt");

        scan(images, objectFile, useProbe, useCaches, saveCaches);
    }

    public void scan(Map<String, BufferedImage> images, File objectFile, boolean useProbe, boolean useCaches, boolean saveCaches) {
        System.out.println("Scanning image " + inputImage.getName() + "...");
        final String prefix = "[" + inputImage.getName() + "] ";

        long start = System.currentTimeMillis();

        ImageCompare imageCompare = new ImageCompare();

        ModifiedDetector modifiedDetector = new ModifiedDetector(inputImage, objectFile);

        LetterGrid grid = imageCompare.getText(inputImage, objectFile, test, images, useProbe, !modifiedDetector.imageChanged() && useCaches, saveCaches);

        System.out.println("Got");

        letterGrid = grid.getLetterGridArray();

        System.out.println("Got griod array");

        text = grid.getPrettyString();

        System.out.println("\n\n" + prefix + "text =\n" + text);

        System.out.println(prefix + "Finished scan in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void highlight(File highlightImagePath) throws IOException {
        this.highlightedFile = new File(highlightImagePath, inputImage.getName().substring(0, inputImage.getName().length() - 4) + "_highlighted.png");


        final String prefix = "[" + inputImage.getName() + "] ";

        System.out.println("\n" + prefix + "Highlighting...");
        long start = System.currentTimeMillis();

        CustomJavaRenderer renderer = new CustomJavaRenderer();
        String highlighted = renderer.highlight(text);

        System.out.println(prefix + "Finished highlighting in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println(prefix + "Modifying letters...");
        start = System.currentTimeMillis();

        LetterFormatter letterFormatter = new LetterFormatter(letterGrid);
        letterFormatter.formatLetters(highlighted);

        System.out.println(prefix + "Finished modifying letters in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println(prefix + "Writing highlighted image to file...");
        start = System.currentTimeMillis();


        letterFileWriter = new LetterFileWriter(letterGrid, inputImage, highlightedFile);
        letterFileWriter.writeToFile(images);

        System.out.println(prefix + "Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");
    }

    public BufferedImage getImage() {
        return this.letterFileWriter.getImage();
    }

    public File getHighlightedFile() {
        return highlightedFile;
    }

    public List<List<Letter>> getLetterGrid() {
        return letterGrid;
    }

    public String getText() {
        return text;
    }
}

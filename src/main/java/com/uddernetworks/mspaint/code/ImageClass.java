package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.code.highlighter.LetterFormatter;
import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.main.*;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.newocr.ScannedImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageClass {

    private File inputImage;
    private ScannedImage scannedImage;
    private String text;
    private LetterFileWriter letterFileWriter;
    private File highlightedFile;
    private MainGUI mainGUI;
    private Main headlessMain;
    private final boolean internal;
    private final boolean useCaches;
    private final boolean saveCaches;

    public ImageClass(File inputImage, MainGUI mainGUI, boolean internal, boolean useCaches, boolean saveCaches) {
        this(inputImage, mainGUI, null, internal, useCaches, saveCaches);
    }

    public ImageClass(File inputImage, MainGUI mainGUI, Main headlessMain, boolean internal, boolean useCaches, boolean saveCaches) {
        this.inputImage = inputImage;
        this.mainGUI = mainGUI;
        this.headlessMain = headlessMain;
        this.internal = internal;
        this.useCaches = useCaches;
        this.saveCaches = saveCaches;

        scan(internal, useCaches, saveCaches);
    }

    public void scan() {
        scan(this.internal, this.useCaches, this.saveCaches);
    }

    public void scan(boolean useCaches, boolean saveCaches) {
        scan(this.internal, useCaches, saveCaches);
    }

    public void scan(boolean internal, boolean useCaches, boolean saveCaches) {
        File cacheFile = CacheUtils.getCacheFor(this.inputImage, internal);
        System.out.println("Scanning image " + inputImage.getName() + "...");
        final String prefix = "[" + inputImage.getName() + "] ";

        long start = System.currentTimeMillis();

        ImageCompare imageCompare = new ImageCompare();

        ModifiedDetector modifiedDetector = new ModifiedDetector(inputImage, cacheFile);

        if (this.headlessMain == null) {
            this.headlessMain = mainGUI.getMain();
        }

        scannedImage = imageCompare.getText(inputImage, cacheFile, mainGUI, this.headlessMain, !modifiedDetector.imageChanged() && useCaches, saveCaches);

        text = scannedImage.getPrettyString();

        System.out.println("\n" + prefix + "text =\n" + text);

        System.out.println(prefix + "Finished scan in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void highlight(File highlightImagePath) throws IOException {
        this.highlightedFile = new File(highlightImagePath, inputImage.getName().substring(0, inputImage.getName().length() - 4) + "_highlighted.png");

        final String prefix = "[" + inputImage.getName() + "] ";

        System.out.println("\n" + prefix + "Highlighting...");
        long start = System.currentTimeMillis();

        LanguageHighlighter highlighter = this.mainGUI.getCurrentLanguage().getLanguageHighlighter();
        String highlighted = highlighter.highlight(text);

        System.out.println(prefix + "Finished highlighting in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println(prefix + "Modifying letters...");
        start = System.currentTimeMillis();

        LetterFormatter letterFormatter = new LetterFormatter(scannedImage);
        letterFormatter.formatLetters(highlighted);

        System.out.println(prefix + "Finished modifying letters in " + (System.currentTimeMillis() - start) + "ms");

        System.out.println(prefix + "Writing highlighted image to file...");
        start = System.currentTimeMillis();


        letterFileWriter = new LetterFileWriter(scannedImage, inputImage, highlightedFile);
        letterFileWriter.writeToFile();

        System.out.println(prefix + "Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");
    }

    public BufferedImage getImage() {
        return this.letterFileWriter.getImage();
    }

    public File getHighlightedFile() {
        return highlightedFile;
    }

    public String getText() {
        return text;
    }

    public File getInputImage() {
        return this.inputImage;
    }

    public ScannedImage getScannedImage() {
        return this.scannedImage;
    }

    public MainGUI getMainGUI() {
        return mainGUI;
    }

    public Main getHeadlessMain() {
        return headlessMain;
    }
}

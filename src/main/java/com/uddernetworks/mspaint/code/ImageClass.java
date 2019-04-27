package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.main.*;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageClass {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageClass.class);

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
        LOGGER.info("Scanning image " + inputImage.getName() + "...");
        final String prefix = "[" + inputImage.getName() + "] ";

        long start = System.currentTimeMillis();

        ImageCompare imageCompare = new ImageCompare();

        ModifiedDetector modifiedDetector = new ModifiedDetector(inputImage, cacheFile);

        if (this.headlessMain == null) {
            this.headlessMain = mainGUI.getMain();
        }

        scannedImage = imageCompare.getText(inputImage, cacheFile, mainGUI, this.headlessMain, !modifiedDetector.imageChanged() && useCaches, saveCaches);

        text = scannedImage.getPrettyString();

        LOGGER.info("\n" + prefix + "text =\n" + text);

        LOGGER.info(prefix + "Finished scan in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void highlight(File highlightImagePath) throws IOException {
        this.highlightedFile = new File(highlightImagePath, inputImage.getName().substring(0, inputImage.getName().length() - 4) + "_highlighted.png");

        final String prefix = "[" + inputImage.getName() + "] ";

        LOGGER.info(prefix + "Highlighting...");
        long start = System.currentTimeMillis();

        new LanguageHighlighter().highlight(this.mainGUI.getCurrentLanguage().getLanguageHighlighter(), this.scannedImage);

        LOGGER.info(prefix + "Finished highlighting in " + (System.currentTimeMillis() - start) + "ms");

        LOGGER.info(prefix + "Writing highlighted image to file...");
        start = System.currentTimeMillis();


        letterFileWriter = new LetterFileWriter(scannedImage, inputImage, highlightedFile);
        letterFileWriter.writeToFile();

        LOGGER.info(prefix + "Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");
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

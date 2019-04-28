package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
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

    public ImageClass(File inputImage, MainGUI mainGUI) {
        this(inputImage, mainGUI, null);
    }

    public ImageClass(File inputImage, MainGUI mainGUI, Main headlessMain) {
        this.inputImage = inputImage;
        this.mainGUI = mainGUI;
        this.headlessMain = headlessMain;
    }

    public void scan() {
        LOGGER.info("Scanning image " + inputImage.getName() + "...");
        final String prefix = "[" + inputImage.getName() + "] ";

        long start = System.currentTimeMillis();

        ImageCompare imageCompare = new ImageCompare();

        if (this.headlessMain == null) {
            this.headlessMain = mainGUI.getMain();
        }

        scannedImage = imageCompare.getText(inputImage, mainGUI, this.headlessMain);

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

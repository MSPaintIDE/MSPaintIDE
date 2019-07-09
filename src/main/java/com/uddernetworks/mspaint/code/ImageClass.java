package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.code.languages.LanguageHighlighter;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.ocr.FontData;
import com.uddernetworks.mspaint.ocr.ImageCompare;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ImageClass {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageClass.class);

    // TODO: Make this configurable?
    public static final boolean AUTO_TRIM_TEXT = true;

    private File inputImage;
    private ScannedImage scannedImage;
    private String text;
    private String trimmedText;
    private LetterFileWriter letterFileWriter;
    private File highlightedFile;
    private MainGUI mainGUI;
    private StartupLogic startupLogic;
    private LanguageHighlighter languageHighlighter;
    private int leadingStripped = 0;

    public ImageClass(File inputImage, MainGUI mainGUI) {
        this(inputImage, mainGUI, null);
        this.startupLogic = this.mainGUI.getStartupLogic();
    }

    public ImageClass(File inputImage, MainGUI mainGUI, StartupLogic startupLogic) {
        this.inputImage = inputImage;
        this.mainGUI = mainGUI;
        this.startupLogic = startupLogic;
        this.languageHighlighter = new LanguageHighlighter();
    }

    public void scan() {
        if (!verifyScannable()) return;

        LOGGER.info("Scanning image " + this.inputImage.getName() + "...");
        final String prefix = "[" + this.inputImage.getName() + "] ";

        long start = System.currentTimeMillis();

        ImageCompare imageCompare = new ImageCompare();

        this.scannedImage = imageCompare.getText(this.inputImage, this.mainGUI, this.startupLogic);

        var leadingStripped = this.scannedImage.stripLeadingSpaces();
        this.text = this.trimmedText = leadingStripped.getPrettyString();
        if (!AUTO_TRIM_TEXT) {
            this.text = this.scannedImage.getPrettyString();
        } else {
            this.leadingStripped = this.scannedImage.getLine(0).size() - leadingStripped.getLine(0).size();
        }

        LOGGER.info("\n" + prefix + "text \n" + this.scannedImage.getPrettyString());

        LOGGER.info(prefix + "Finished scan in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void highlight(File highlightImagePath) throws IOException {
        this.highlightedFile = new File(highlightImagePath, inputImage.getName().substring(0, inputImage.getName().length() - 4) + "_highlighted.png");

        final String prefix = "[" + inputImage.getName() + "] ";

        LOGGER.info(prefix + "Highlighting...");
        long start = System.currentTimeMillis();
        this.languageHighlighter.highlight(this.startupLogic.getCurrentLanguage(), this);

        LOGGER.info(prefix + "Finished highlighting in " + (System.currentTimeMillis() - start) + "ms");

        LOGGER.info(prefix + "Writing highlighted image to file...");
        start = System.currentTimeMillis();

        letterFileWriter = new LetterFileWriter(scannedImage, inputImage, highlightedFile);
        letterFileWriter.writeToFile();

        LOGGER.info(prefix + "Finished writing to file in " + (System.currentTimeMillis() - start) + "ms");
    }

    private boolean verifyScannable() {
        FontData fontData;
        var ocrManager = this.startupLogic.getOCRManager();
        if (ocrManager == null || (fontData = ocrManager.getActiveFont()) == null || !fontData.getDatabaseManager().isTrainedSync()) {
            LOGGER.warn("Cancelling the scanning of {} as the current font is either non existent or untrained", this.getInputImage().getName());
            return false;
        }

        return true;
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

    public String getTrimmedText() {
        return trimmedText;
    }

    public File getInputImage() {
        return this.inputImage;
    }

    public Optional<ScannedImage> getScannedImage() {
        return Optional.ofNullable(this.scannedImage);
    }

    public MainGUI getMainGUI() {
        return mainGUI;
    }

    public StartupLogic getStartupLogic() {
        return startupLogic;
    }

    public int getLeadingStripped() {
        return leadingStripped;
    }
}

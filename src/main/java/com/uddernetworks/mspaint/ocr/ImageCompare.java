package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ImageCompare {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageCompare.class);

    public ScannedImage getText(File inputImage, MainGUI mainGUI, Main main) {
        try {
            if (!MainGUI.HEADLESS) {
                mainGUI.setStatusText("Scanning image " + inputImage.getName() + "...");
                mainGUI.setIndeterminate(true);
            }

            return main.getOCRManager().getActiveFont().getScan().scanImage(inputImage);
        } finally {
            if (!MainGUI.HEADLESS) {
                mainGUI.setStatusText("");
                mainGUI.setIndeterminate(false);
            }
        }
    }
}
package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ImageCompare {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageCompare.class);
    private CountDownLatch latch = new CountDownLatch(1);

    private static String getMD5(File file) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(file));
        } catch (IOException e) {
            return file.getAbsolutePath().replace(File.separator, "_");
        }
    }

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
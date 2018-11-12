package com.uddernetworks.mspaint.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.newocr.OCRHandle;
import com.uddernetworks.newocr.ScannedImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map;

public class ImageCompare {

    public ScannedImage getText(File inputImage, File objectFile, MainGUI mainGUI, Main main, boolean readFromFile, boolean saveCaches) {
        if (readFromFile) {
            System.out.println("Image not changed since file changed, so using file...");
        } else {
            System.out.println("Image has changed since last write to data file, reading image...");
        }

        ScannedImage scannedImage;

        try {
            if (objectFile != null && !objectFile.exists() && !objectFile.isFile()) {
                try {
                    objectFile.getParentFile().mkdirs();
                    readFromFile = !objectFile.createNewFile();
                } catch (IOException ignored) {
                    readFromFile = false;
                }
            }

            if (!readFromFile) {
                if (!MainGUI.HEADLESS) mainGUI.setStatusText("Scanning image " + inputImage.getName() + "...");

                if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(true);

                OCRHandle ocrHandle = new OCRHandle(main.getDatabaseManager());
                scannedImage = ocrHandle.scanImage(inputImage);

                if (saveCaches) {
                    if (!MainGUI.HEADLESS) {
                        mainGUI.setStatusText("Saving to cache file...");
                    }

                    Files.write(objectFile.toPath(), new Gson().toJson(scannedImage).getBytes());
                }

                if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(false);
            } else {
                GsonBuilder gsonBuilder = new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Map.Entry.class, (InstanceCreator<Map.Entry>) type -> new AbstractMap.SimpleEntry(null, null));

                scannedImage = gsonBuilder.create().fromJson(new String(Files.readAllBytes(objectFile.toPath())), ScannedImage.class);
            }

            return scannedImage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

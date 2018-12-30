package com.uddernetworks.mspaint.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonParseException;
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
        ScannedImage scannedImage;

        try {
            if (/* readFromFile &&*/ objectFile != null && !objectFile.isFile()) {
                try {
                    objectFile.getParentFile().mkdirs();
                    readFromFile = !objectFile.createNewFile();
                } catch (IOException ignored) {
                    readFromFile = false;
                }
            }

            System.out.println("Reading from file: " + readFromFile);

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
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();

                    gsonBuilder.registerTypeAdapter(Map.Entry.class, (InstanceCreator<Map.Entry>) type -> new AbstractMap.SimpleEntry<>(null, null));

                    scannedImage = gsonBuilder.create().fromJson(new String(Files.readAllBytes(objectFile.toPath())), ScannedImage.class);
                } catch (JsonParseException e) {
                    if (!MainGUI.HEADLESS) mainGUI.setHaveError();
                    System.err.println("There was a problem reading the cache for " + inputImage.getName() + "! Try resetting caches.");
                    scannedImage = null;
                }
            }

            return scannedImage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

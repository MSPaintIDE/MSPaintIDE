package com.uddernetworks.mspaint.ocr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonParseException;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Map;

public class ImageCompare {

    private static Logger LOGGER = LoggerFactory.getLogger(ImageCompare.class);

    public ScannedImage getText(File inputImage, File cacheFile, MainGUI mainGUI, Main main, boolean readFromCache, boolean saveCaches) {
        ScannedImage scannedImage;

        try {
            if (readFromCache && cacheFile != null && !cacheFile.isFile()) {
                try {
                    cacheFile.getParentFile().mkdirs();
                    readFromCache = !cacheFile.createNewFile();
                } catch (IOException ignored) {
                    readFromCache = false;
                }
            }

            if (!readFromCache) {
                if (!MainGUI.HEADLESS) mainGUI.setStatusText("Scanning image " + inputImage.getName() + "...");

                if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(true);

                scannedImage = main.getOCRManager().getActiveFont().getScan().scanImage(inputImage);

                if (saveCaches) {
                    if (!MainGUI.HEADLESS) {
                        mainGUI.setStatusText("Saving to cache file...");
                    }

                    Files.write(cacheFile.toPath(), new Gson().toJson(scannedImage).getBytes());
                }

                if (!MainGUI.HEADLESS) mainGUI.setIndeterminate(false);
            } else {
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();

                    gsonBuilder.registerTypeAdapter(Map.Entry.class, (InstanceCreator<Map.Entry>) type -> new AbstractMap.SimpleEntry<>(null, null));

                    scannedImage = gsonBuilder.create().fromJson(new String(Files.readAllBytes(cacheFile.toPath())), ScannedImage.class);
                } catch (JsonParseException e) {
                    if (!MainGUI.HEADLESS) mainGUI.setHaveError();
                    LOGGER.error("There was a problem reading the cache for " + inputImage.getName() + "! Try resetting caches.");
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

package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.newocr.configuration.ConfigReflectionCacher;
import com.uddernetworks.newocr.configuration.ReflectionCacher;
import com.uddernetworks.newocr.recognition.Actions;
import com.uddernetworks.newocr.recognition.Scan;
import com.uddernetworks.newocr.recognition.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OCRManager {

    private static Logger LOGGER = LoggerFactory.getLogger(OCRManager.class);

    private ReflectionCacher reflectionCacher;
    private Map<String, FontData> fontDataMap;

    private FontData activeFont;
    private Main main;

    public OCRManager(Main main) {
        this.reflectionCacher = new ConfigReflectionCacher();
        this.fontDataMap = new HashMap<>();
        this.main = main;
    }

    public void setActiveFont(String name, String config) {
        if (!this.fontDataMap.containsKey(name)) {
            LOGGER.info("Loading initial font data for " + name + " at " + config);
            (this.activeFont = new FontData(this, name, config)).initialize();
        } else {
            this.activeFont = this.fontDataMap.get(name);
        }
    }

    public Scan getScan() {
        return this.activeFont.getScan();
    }

    public Train getTrain() {
        return this.activeFont.getTrain();
    }

    public Actions getActions() {
        return this.activeFont.getActions();
    }

    public ReflectionCacher getReflectionCacher() {
        return reflectionCacher;
    }

    public FontData getActiveFont() {
        return this.activeFont;
    }
}

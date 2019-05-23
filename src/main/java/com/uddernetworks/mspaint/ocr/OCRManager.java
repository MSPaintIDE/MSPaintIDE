package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.newocr.configuration.ConfigReflectionCacher;
import com.uddernetworks.newocr.configuration.ReflectionCacher;
import com.uddernetworks.newocr.recognition.Actions;
import com.uddernetworks.newocr.recognition.Scan;
import com.uddernetworks.newocr.recognition.ScannedImage;
import com.uddernetworks.newocr.recognition.Train;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OCRManager {

    private static Logger LOGGER = LoggerFactory.getLogger(OCRManager.class);

    private ReflectionCacher reflectionCacher;
    private Map<String, FontData> fontDataMap;

    private FontData activeFont;
    private StartupLogic startupLogic;

    public OCRManager(StartupLogic startupLogic) {
        this.reflectionCacher = new ConfigReflectionCacher();
        this.fontDataMap = new HashMap<>();
        this.startupLogic = startupLogic;
    }

    public void setActiveFont(String name, String config) {
        System.out.println("3245345 name = " + name);
        if (!this.fontDataMap.containsKey(name)) {
            LOGGER.info("Loading initial font data for " + name + " at " + config);
            (this.activeFont = new FontData(this, name, config)).initialize();
        } else {
            this.activeFont = this.fontDataMap.get(name);
        }
    }

    public double getFontSize(ScannedImage scannedImage) {
        var descriptiveStatistics = new DescriptiveStatistics();

        var sizes = scannedImage
                .getGrid()
                .values()
                .stream()
                .flatMap(List::stream)
                .filter(imageLetter -> imageLetter.getLetter() != ' ')
                .map(getActions()::getFontSize)
                .filter(OptionalDouble::isPresent)
                .map(OptionalDouble::getAsDouble)
                .peek(descriptiveStatistics::addValue)
                .collect(Collectors.toCollection(DoubleArrayList::new));

        var lowerBound = descriptiveStatistics.getPercentile(20);
        var upperBound = descriptiveStatistics.getPercentile(80);

        sizes.removeIf((Predicate<Double>) value -> value > upperBound || value < lowerBound);

        return sizes.stream().mapToDouble(Double::valueOf).average().orElse(0D);
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

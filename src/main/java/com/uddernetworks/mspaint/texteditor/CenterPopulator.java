package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.ocr.FontData;
import com.uddernetworks.newocr.detection.SearchImage;
import com.uddernetworks.newocr.recognition.Actions;
import com.uddernetworks.newocr.recognition.OCRScan;
import com.uddernetworks.newocr.utils.IntPair;
import com.uddernetworks.newocr.utils.OCRUtils;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CenterPopulator {

    private final Char2IntMap def = new Char2IntOpenHashMap();

    private Map<FontData, Int2ObjectMap<Char2IntMap>> centers = new HashMap<>();

    private Main main;

    public CenterPopulator(Main main) {
        this.main = main;
    }

    // Code loosely adapted from com.uddernetworks.newocr.OCRHandle.java
    // TODO: Clean this up a ton :(
    public void generateCenters(int fontSize) throws IOException {
        var activeFont = this.main.getOCRManager().getActiveFont();

        if (centers.containsKey(activeFont) && centers.get(activeFont).containsKey(fontSize)) return;

        var currentCenters = new Char2IntOpenHashMap();
        centers.computeIfAbsent(activeFont, x -> new Int2ObjectOpenHashMap<>()).put(fontSize, currentCenters);

        var input = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        var graphics = input.createGraphics();

        clearImage(input);

        var rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHints(rht);

        var font = new Font(activeFont.getFontName(), Font.PLAIN, fontSize);
        graphics.setFont(font);

        input = new BufferedImage(graphics.getFontMetrics().stringWidth(OCRScan.RAW_STRING) + 50, fontSize * 2, BufferedImage.TYPE_INT_ARGB);;

        graphics = input.createGraphics();
        graphics.setRenderingHints(rht);
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);

        clearImage(input);

        graphics.drawString(OCRScan.RAW_STRING, 10, fontSize);

        var centerCalcFile = new File(System.getProperty("java.io.tmpdir"), "center_calc.png");

        ImageIO.write(input, "png", centerCalcFile);

        // Goes through coordinates of image and adds any connecting pixels to `coordinates`
        var scanImage = activeFont.getScan().scanImage(centerCalcFile);

        var currentLetter = new AtomicInteger();

        var lineBound = getLineBounds(input, activeFont.getActions());

        scanImage.getGridLineAtIndex(0).get().forEach(searchCharacter -> {
            var halfOfLineHeight = ((double) lineBound.getValue() - (double) lineBound.getKey()) / 2;
            var middleToTopChar = (double) searchCharacter.getY() - (double) lineBound.getKey();
            var topOfLetterToCenter = halfOfLineHeight - middleToTopChar;

            currentCenters.put(OCRScan.RAW_STRING.charAt(currentLetter.getAndIncrement()), (int) topOfLetterToCenter);
        });

        Files.deleteIfExists(centerCalcFile.toPath());
    }

    private IntPair getLineBounds(BufferedImage input, Actions actions) {
        OCRUtils.filter(input);

        var values = OCRUtils.createGrid(input);

        OCRUtils.toGrid(input, values);

        var searchImage = new SearchImage(values);
        return actions.getLineBoundsForTraining(searchImage).get(0);
    }

    public int getCenter(char cha, int fontSize) {
        var activeFont = this.main.getOCRManager().getActiveFont();
        if (!centers.containsKey(activeFont)) return 0;
        return centers.get(activeFont).getOrDefault(fontSize, def).getOrDefault(cha, 0);
    }

    private void clearImage(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

}

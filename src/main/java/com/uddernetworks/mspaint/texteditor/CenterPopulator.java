package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.main.StartupLogic;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CenterPopulator {

    private final Char2IntMap def = new Char2IntOpenHashMap();

    private Map<FontData, Int2ObjectMap<Char2IntMap>> centers = new HashMap<>();

    private StartupLogic startupLogic;

    public CenterPopulator(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    // Code loosely adapted from com.uddernetworks.newocr.OCRHandle.java
    // TODO: Clean this up a ton :(
    public void generateCenters(int fontSize) throws IOException {
        var activeFont = this.startupLogic.getOCRManager().getActiveFont();

        if (centers.containsKey(activeFont) && centers.get(activeFont).containsKey(fontSize)) return;

        var currentCenters = new Char2IntOpenHashMap();
        centers.computeIfAbsent(activeFont, x -> new Int2ObjectOpenHashMap<>()).put(fontSize, currentCenters);

        var input = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        var graphics = input.createGraphics();

        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        var font = new Font(activeFont.getFontName(), Font.PLAIN, fontSize);
        graphics.setFont(font);

        input = new BufferedImage(graphics.getFontMetrics().stringWidth(OCRScan.RAW_STRING) + 50, fontSize * 2, BufferedImage.TYPE_INT_ARGB);

        graphics = input.createGraphics();

        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        graphics.setFont(font);
        graphics.setColor(Color.BLACK);

        clearImage(input);

        graphics.drawString(OCRScan.RAW_STRING, 10, fontSize);

        var centerCalcFile = new File(System.getProperty("java.io.tmpdir"), "center_calc.png");

        ImageIO.write(input, "png", centerCalcFile);

        // Goes through coordinates of image and adds any connecting pixels to `coordinates`
        var scanImage = activeFont.getScan().scanImage(centerCalcFile).stripLeadingSpaces();

        var currentLetter = new AtomicInteger();

        var lineBound = getLineBounds(input, activeFont.getActions());

        scanImage.getGridLineAtIndex(0).get().forEach(imageLetter -> {
            if (currentLetter.get() == OCRScan.RAW_STRING.indexOf("W W")) return;
            var letter = OCRScan.RAW_STRING.charAt(currentLetter.getAndIncrement());
            var topOfLetterToCenter = imageLetter.getY() - lineBound.getKey();

            // Extra space above character to Y
            currentCenters.put(letter, topOfLetterToCenter);
        });
    }

    private IntPair getLineBounds(BufferedImage input, Actions actions) {
        OCRUtils.filter(input);

        var values = OCRUtils.createGrid(input);

        OCRUtils.toGrid(input, values);

        var searchImage = new SearchImage(values);
        return actions.getLineBoundsForTraining(searchImage).get(0);
    }

    public int getCenter(char cha, int fontSize) {
        var activeFont = this.startupLogic.getOCRManager().getActiveFont();
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

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
        try {
            new Exception("shit").printStackTrace();
            var activeFont = this.main.getOCRManager().getActiveFont();

            if (centers.containsKey(activeFont) && centers.get(activeFont).containsKey(fontSize)) return;

            var currentCenters = new Char2IntOpenHashMap();
            centers.computeIfAbsent(activeFont, x -> new Int2ObjectOpenHashMap<>()).put(fontSize, currentCenters);

            var input = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
            var graphics = input.createGraphics();

            clearImage(input);

            var rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            graphics.setRenderingHints(rht);

            System.out.println("fontSize = " + fontSize);
            System.out.println("shit " + activeFont.getFontName());
            System.out.println("shit " + activeFont.getConfigPath());
            var font = new Font("Verdana", Font.PLAIN, fontSize);
            System.out.println("111");
            graphics.setFont(font);
            System.out.println("222");
            System.out.println("333");
//            System.out.println(new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY));
//            System.out.println(new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY).createGraphics());

//            var constructor = FontDesignMetrics.class.getDeclaredConstructor(Font.class);
//            constructor.setAccessible(true);
//            FontDesignMetrics metrics = constructor.newInstance(font);

            try {
                GraphicsEnvironment ge =
                        GraphicsEnvironment.getLocalGraphicsEnvironment();
                var ni = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Program Files (x86)\\MS Paint IDE\\runtime\\lib\\fonts\\comic.ttf"));
                ge.registerFont(ni);

                var metrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font("Verdana", Font.PLAIN, fontSize));
                System.out.println("REALLLL metrics = " + metrics);

            } catch (IOException|FontFormatException e) {
                //Handle exception
            }

            FontMetrics metrics = null;

            System.out.println(metrics);
            System.out.println("222.5");
            System.out.println("Making " + metrics.stringWidth(OCRScan.RAW_STRING) + 50 + " x " + (fontSize * 2));
            input = new BufferedImage(metrics.stringWidth(OCRScan.RAW_STRING) + 50, fontSize * 2, BufferedImage.TYPE_INT_ARGB);
            System.out.println("333");

            graphics = input.createGraphics();
            System.out.println("444");
            graphics.setRenderingHints(rht);
            System.out.println("555");
            graphics.setFont(font);
            System.out.println("666");
            graphics.setColor(Color.BLACK);
            System.out.println("777");

            clearImage(input);
            System.out.println("888");

            graphics.drawString(OCRScan.RAW_STRING, 10, fontSize);
            System.out.println("999");

            var centerCalcFile = new File(System.getProperty("java.io.tmpdir"), "center_calc.png");
            System.out.println("10 10 10");

            ImageIO.write(input, "png", centerCalcFile);
            System.out.println("11 11 11");

            // Goes through coordinates of image and adds any connecting pixels to `coordinates`
            var scanImage = activeFont.getScan().scanImage(centerCalcFile);
            System.out.println("12 12 12");

            var currentLetter = new AtomicInteger();
            System.out.println("13 13 13");

            var lineBound = getLineBounds(input, activeFont.getActions());
            System.out.println("14 14 14");
            System.out.println("lineBound = " + lineBound);

            scanImage.getGridLineAtIndex(0).get().forEach(searchCharacter -> {
//            System.out.println(searchCharacter.getX());
                var halfOfLineHeight = ((double) lineBound.getValue() - (double) lineBound.getKey()) / 2;
                var middleToTopChar = (double) searchCharacter.getY() - (double) lineBound.getKey();
                var topOfLetterToCenter = halfOfLineHeight - middleToTopChar;

                var letter = OCRScan.RAW_STRING.charAt(currentLetter.getAndIncrement());
                System.out.println(letter);

                if (letter == 'e') {
                    System.out.println("halfOfLineHeight = " + halfOfLineHeight);
                    System.out.println("middleToTopChar = " + middleToTopChar + "(" + searchCharacter.getY() + " - " + lineBound.getKey() + ")");
                    System.out.println("topOfLetterToCenter = " + topOfLetterToCenter);

                    OCRUtils.printOut(searchCharacter.getValues());
                }

                currentCenters.put(letter, (int) topOfLetterToCenter);
//            System.out.println(topOfLetterToCenter);
            });

            System.out.println("centerCalcFile = " + centerCalcFile.getAbsolutePath());
//        Files.deleteIfExists(centerCalcFile.toPath());

            System.out.println("currentCenters = " + currentCenters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private FontMetrics getMetrics() {
//        return new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY).createGraphics();
//    }

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

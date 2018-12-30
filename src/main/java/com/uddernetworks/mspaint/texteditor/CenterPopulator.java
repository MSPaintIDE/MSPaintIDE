package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.newocr.OCRHandle;
import com.uddernetworks.newocr.SearchImage;
import com.uddernetworks.newocr.character.SearchCharacter;
import com.uddernetworks.newocr.utils.OCRUtils;
import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CenterPopulator {

    private Map<Character, Integer> def = new HashMap<>();
    private Map<Integer, Map<Character, Integer>> centers = new HashMap<>();

    // Code loosely adapted from com.uddernetworks.newocr.OCRHandle.java
    // TODO: Clean this up a ton
    public void generateCenters(int fontSize) {
        Map<Character, Integer> currentCenters = new HashMap<>();
        centers.put(fontSize, currentCenters);


        BufferedImage input = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = input.createGraphics();

        clearImage(input);

        RenderingHints rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rht);

        Font font = new Font("Verdana", Font.PLAIN, fontSize);
        graphics.setFont(font);

        String drawString = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghjiklmnopqrstuvwxyz{|}~";

        input = new BufferedImage(graphics.getFontMetrics().stringWidth(drawString) + 50, fontSize * 2, BufferedImage.TYPE_INT_ARGB);;
        graphics = input.createGraphics();
        graphics.setRenderingHints(rht);

        graphics.setFont(font);
        graphics.setColor(Color.BLACK);

        clearImage(input);

        graphics.drawString(drawString, 10, fontSize);

        OCRUtils.filter(input);

        boolean[][] values = OCRUtils.createGrid(input);
        List<SearchCharacter> searchCharacters = new ArrayList<>();

        OCRUtils.toGrid(input, values);

        Pair<Integer, Integer> lineBound = OCRHandle.getLineBoundsForTesting(values).get(0);

        SearchImage searchImage = new SearchImage(values);

        List<Map.Entry<Integer, Integer>> coordinates = new ArrayList<>();

        // Goes through coordinates of image and adds any connecting pixels to `coordinates`

        for (int y = input.getHeight(); 0 <= --y; ) {
            for (int x = 0; x < input.getWidth(); x++) {
                OCRHandle.getLetterFrom(searchImage, x, y, coordinates, searchCharacters);
            }
        }

//         Gets all characters found at the line bounds from the searchCharacters (Collected from the double for loops)
        List<SearchCharacter> line = OCRUtils.findCharactersAtLine(lineBound.getKey(), lineBound.getValue(), searchCharacters);

        AtomicInteger currentLetter = new AtomicInteger(0);
        line.forEach(searchCharacter -> {
            double halfOfLineHeight = ((double) lineBound.getValue() - (double) lineBound.getKey()) / 2;
            double middleToTopChar = (double) searchCharacter.getY() - (double) lineBound.getKey();
            double topOfLetterToCenter = halfOfLineHeight - middleToTopChar;

            currentCenters.put(drawString.charAt(currentLetter.getAndIncrement()), (int) topOfLetterToCenter);
        });
    }

    public int getCenter(char cha, int fontSize) {
        return centers.getOrDefault(fontSize, def).getOrDefault(cha, 0);
    }

    private void clearImage(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

}

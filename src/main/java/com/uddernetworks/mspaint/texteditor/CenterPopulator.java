package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.newocr.OCRHandle;
import com.uddernetworks.newocr.SearchImage;
import com.uddernetworks.newocr.character.SearchCharacter;
import com.uddernetworks.newocr.utils.IntPair;
import com.uddernetworks.newocr.utils.OCRUtils;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CenterPopulator {

    private Char2IntMap def = new Char2IntOpenHashMap();
    
    private Int2ObjectMap<Char2IntMap> centers = new Int2ObjectOpenHashMap<>();

    // Code loosely adapted from com.uddernetworks.newocr.OCRHandle.java
    // TODO: Clean this up a ton
    public void generateCenters(int fontSize) {
        var currentCenters = new Char2IntOpenHashMap();
        centers.put(fontSize, currentCenters);
        
        var input = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        var graphics = input.createGraphics();

        clearImage(input);

        var rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rht);

        var font = new Font("Verdana", Font.PLAIN, fontSize);
        graphics.setFont(font);

        var drawString = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghjiklmnopqrstuvwxyz{|}~";

        input = new BufferedImage(graphics.getFontMetrics().stringWidth(drawString) + 50, fontSize * 2, BufferedImage.TYPE_INT_ARGB);;
        
        graphics = input.createGraphics();
        graphics.setRenderingHints(rht);
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);

        clearImage(input);

        graphics.drawString(drawString, 10, fontSize);

        OCRUtils.filter(input);

        var values = OCRUtils.createGrid(input);
        var searchCharacters = new ArrayList<SearchCharacter>();

        OCRUtils.toGrid(input, values);

        var lineBound = OCRHandle.getLineBoundsForTesting(values).get(0);
        var searchImage = new SearchImage(values);
        var coordinates = new ArrayList<IntPair>();

        // Goes through coordinates of image and adds any connecting pixels to `coordinates`
        for (int y = input.getHeight(); 0 <= --y; ) {
            for (int x = 0; x < input.getWidth(); x++) {
                OCRHandle.getLetterFrom(searchImage, x, y, coordinates, searchCharacters);
            }
        }

        // Gets all characters found at the line bounds from the searchCharacters (Collected from the double for loops)
        var line = OCRUtils.findCharactersAtLine(lineBound.getKey(), lineBound.getValue(), searchCharacters);

        var currentLetter = new AtomicInteger();
        
        line.forEach(searchCharacter -> {
            var halfOfLineHeight = ((double) lineBound.getValue() - (double) lineBound.getKey()) / 2;
            var middleToTopChar = (double) searchCharacter.getY() - (double) lineBound.getKey();
            var topOfLetterToCenter = halfOfLineHeight - middleToTopChar;

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

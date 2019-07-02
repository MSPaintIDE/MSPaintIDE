package com.uddernetworks.mspaint.gui.window.search;

import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.texteditor.LetterGenerator;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.ScannedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReplaceManager {

    private static Logger LOGGER = LoggerFactory.getLogger(ReplaceManager.class);

    private static final int WHITE = Color.WHITE.getRGB();
    private MainGUI mainGUI;

    public ReplaceManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public void replaceText(SearchResult searchResult, String text) throws IOException, ExecutionException, InterruptedException {
        var ocrManager = this.mainGUI.getStartupLogic().getOCRManager();
        ScannedImage scannedImage = searchResult.getScannedImage();

        int size = (int) Math.round(ocrManager.getFontSize(scannedImage));

        LetterGenerator letterGenerator = new LetterGenerator();
        var spaceOptional = this.mainGUI.getStartupLogic().getOCRManager().getActiveFont().getDatabaseManager().getAllCharacterSegments().get().stream().filter(databaseCharacter -> databaseCharacter.getLetter() == ' ').findFirst();

        if (spaceOptional.isEmpty()) {
            LOGGER.error("Couldn't find space for size: " + size);
            return;
        }

        var space = spaceOptional.get();

        double spaceRatio = space.getAvgWidth() / space.getAvgHeight();
        int characterBetweenSpace = (int) ((spaceRatio * size) / 3);


        // Get real (non-binary) pixel data for for ImageLetters

        var original = scannedImage.getOriginalImage();

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                var originalColor = new Color(original.getRGB(x, y));
                var use = (int) Math.round((originalColor.getRed() + originalColor.getGreen() + originalColor.getBlue()) / 3D);
                original.setRGB(x, y, new Color(use, use, use).getRGB());
            }
        }

        for (int lineNum = 0; lineNum < scannedImage.getLineCount(); lineNum++) {
            scannedImage.getGridLineAtIndex(lineNum).ifPresent(line -> {
                line.forEach(letter -> {
                    var image = original.getSubimage(letter.getX(), letter.getY(), letter.getWidth(), letter.getHeight());
                    image = grabRealSub(original, letter);
                    image = trimImage(image);
                    var values = LetterGenerator.createGrid(image);
                    LetterGenerator.toGrid(image, values);

                    var grid = LetterGenerator.trim(values);

                    letter.setValues(LetterGenerator.doubleToBooleanGrid(grid));
                    letter.setData(grid);
                });
            });
        }

        var centerPopulator = this.mainGUI.getStartupLogic().getCenterPopulator();
        centerPopulator.generateCenters(size);

        int foundPos = searchResult.getFoundPosition();
        var lineEntry = scannedImage.getLineEntry(searchResult.getLineNumber() - 1);
        List<ImageLetter> line = lineEntry.getValue();
        int lineY = lineEntry.getKey();

        final int imageLettersSize = searchResult.getImageLetters().size();

        int x = line.get(foundPos).getX();

        int farRight = line.get(foundPos + imageLettersSize).getX() - x;

        for (int i = 0; i < imageLettersSize; i++) {
            line.remove(foundPos);
        }

        int addBy = 0;

        line.subList(foundPos, line.size()).forEach(imageLetter -> imageLetter.setX(imageLetter.getX() - farRight));

        List<ImageLetter> adding = new ArrayList<>();

        for (int i = 0; i < text.toCharArray().length; i++) {
            x += addBy;
            char cha = text.charAt(i);

            if (cha == ' ') {
                addBy = (int) Math.floor(spaceRatio * size) - characterBetweenSpace;
            } else {
                var letterGrid = letterGenerator.generateCharacter(cha, size, ocrManager.getActiveFont(), space);
                int center = centerPopulator.getCenter(cha, size);

                ImageLetter letter = new ImageLetter(cha, 0, x, (int) Math.round(lineY + center - (size / 2D)), letterGrid[0].length, letterGrid.length, 0D, 0D, 0D);
                letter.setValues(LetterGenerator.doubleToBooleanGrid(letterGrid));
                letter.setData(letterGrid);
                adding.add(letter);

                addBy = letterGrid[0].length + characterBetweenSpace;
            }

            int finalAddBy = addBy;
            line.subList(foundPos, line.size()).forEach(imageLetter -> imageLetter.setX(imageLetter.getX() + finalAddBy));
        }

        line.addAll(adding);

        BufferedImage bufferedImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(scannedImage, bufferedImage, searchResult.getFile());
        letterFileWriter.writeToFile();
    }

    // TODO: Change the following methods in this commit to use 2D arrays, not BufferedImages
    public static BufferedImage grabRealSub(BufferedImage original, ImageLetter imageLetter) {
        var sub = original.getSubimage(imageLetter.getX(), imageLetter.getY(), imageLetter.getWidth(), imageLetter.getHeight());
        return expandToWhite(original, sub, imageLetter);
    }

    public static BufferedImage expandToWhite(BufferedImage original, BufferedImage input, ImageLetter imageLetter) {
        var modded = false;
        if (yHasBlack(input, 0)) {
            if (expandUp(imageLetter)) modded = true;
        }

        if (!modded && yHasBlack(input, input.getHeight() - 1)) {
            if (expandDown(original, imageLetter)) modded = true;
        }

        if (!modded && xHasBlack(input, 0)) {
            if (expandLeft(imageLetter)) modded = true;
        }

        if (!modded && xHasBlack(input, input.getWidth() - 1)) {
            if (expandRight(original, imageLetter)) modded = true;
        }

        if (modded)
            input = expandToWhite(original, original.getSubimage(imageLetter.getX(), imageLetter.getY(), imageLetter.getWidth(), imageLetter.getHeight()), imageLetter);
        return input;
    }

    public static BufferedImage trimImage(BufferedImage image) {
        if (!yHasBlack(image, 0)) image = image.getSubimage(0, 1, image.getWidth(), image.getHeight() - 1);
        if (!yHasBlack(image, image.getHeight() - 1))
            image = image.getSubimage(0, 0, image.getWidth(), image.getHeight() - 1);
        if (!xHasBlack(image, 0)) image = image.getSubimage(1, 0, image.getWidth() - 1, image.getHeight());
        if (!xHasBlack(image, image.getWidth() - 1))
            image = image.getSubimage(0, 0, image.getWidth() - 1, image.getHeight());

        var width = image.getWidth();
        var height = image.getHeight();

        while (image.getWidth() != width && image.getHeight() != height) {
            width = image.getWidth();
            height = image.getHeight();
            image = trimImage(image);
        }

        return image;
    }

    // Return: successful
    public static boolean expandUp(ImageLetter imageLetter) {
        if (imageLetter.getY() == 0) return false;
        imageLetter.setY(imageLetter.getY() - 1);
        imageLetter.setHeight(imageLetter.getHeight() + 1);
        return true;
    }

    public static boolean expandDown(BufferedImage original, ImageLetter imageLetter) {
        if (imageLetter.getY() + imageLetter.getHeight() >= original.getHeight()) return false;
        imageLetter.setHeight(imageLetter.getHeight() + 1);
        return true;
    }

    public static boolean expandLeft(ImageLetter imageLetter) {
        if (imageLetter.getX() == 0) return false;
        imageLetter.setX(imageLetter.getX() - 1);
        imageLetter.setWidth(imageLetter.getWidth() + 1);
        return true;
    }

    public static boolean expandRight(BufferedImage original, ImageLetter imageLetter) {
        if (imageLetter.getX() + imageLetter.getWidth() >= original.getWidth()) return false;
        imageLetter.setWidth(imageLetter.getWidth() + 1);
        return true;
    }

    public static boolean yHasBlack(BufferedImage input, int y) {
        for (int x = 0; x < input.getWidth(); x++) {
            if (input.getRGB(x, y) != WHITE) return true;
        }
        return false;
    }

    public static boolean xHasBlack(BufferedImage input, int x) {
        for (int y = 0; y < input.getHeight(); y++) {
            if (input.getRGB(x, y) != WHITE) return true;
        }
        return false;
    }
}

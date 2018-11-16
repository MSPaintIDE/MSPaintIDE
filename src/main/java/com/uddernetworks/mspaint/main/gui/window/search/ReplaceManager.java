package com.uddernetworks.mspaint.main.gui.window.search;

import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.mspaint.texteditor.CenterPopulator;
import com.uddernetworks.mspaint.texteditor.LetterGenerator;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import com.uddernetworks.newocr.DatabaseCharacter;
import com.uddernetworks.newocr.ImageLetter;
import com.uddernetworks.newocr.ScannedImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReplaceManager {


    private MainGUI mainGUI;

    public ReplaceManager(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    public void replaceText(SearchResult searchResult, String text) throws IOException, ExecutionException, InterruptedException {
        ScannedImage scannedImage = searchResult.getScannedImage();
        LetterGenerator letterGenerator = new LetterGenerator(this.mainGUI.getMain());
        double size = SettingsManager.getSetting(Setting.EDIT_FILE_SIZE, Integer.class);
        List<DatabaseCharacter> databaseCharacters = this.mainGUI.getMain().getDatabaseManager().getAllCharacterSegments(TextEditorManager.matchNearestFontSize((int) size)).get();
        DatabaseCharacter space = databaseCharacters
                .stream()
                .filter(databaseCharacter -> databaseCharacter.getLetter() == ' ')
                .findFirst()
                .orElse(null);

        if (space == null) {
            System.err.println("Couldn't find space for size: " + size);
            return;
        }

        double spaceRatio = space.getAvgWidth() / space.getAvgHeight();
        int characterBetweenSpace = (int) ((spaceRatio * size) / 3);

        CenterPopulator centerPopulator = new CenterPopulator();
        centerPopulator.generateCenters((int) size);

        int foundPos = searchResult.getFoundPosition();
        Map.Entry<Integer, List<ImageLetter>> lineEntry = scannedImage.getLineEntry(searchResult.getLineNumber() - 1);
        List<ImageLetter> line = lineEntry.getValue();
        int lineY = lineEntry.getKey();

        System.out.println("lineY = " + lineY);

        final int sizee = searchResult.getImageLetters().size();
//        IntStream.range(foundPos, foundPos + sizee).forEach(line::remove);
//        IntStream.rangeClosed(foundPos, foundPos + sizee).forEach(line::remove);

        System.out.println("sizee = " + sizee);

        int x = line.get(foundPos).getX();

        int farRight = line.get(foundPos + sizee).getX() - x;
        System.out.println("farRight = " + farRight);

        for (int i = 0; i < sizee; i++) {
            line.remove(foundPos);
        }

        System.out.println("foundPos = " + foundPos);
//        line.remove(foundPos);
//        for (int i = 0; i < sizee; i++) {
//            line.remove(foundPos + i);
//        }
        System.out.println("orig x = " + x);

        int addBy = 0;
//        line.stream().skip(foundPos + text.length() + 1).forEach(imageLetter -> imageLetter.setX(imageLetter.getX() - farRight));

        line.forEach(imageLetter -> imageLetter.setX(imageLetter.getX() - farRight));

        List<ImageLetter> adding = new ArrayList<>();

        for (int i = 0; i < text.toCharArray().length; i++) {
            x += addBy;
            char cha = text.charAt(i);
            boolean[][] letterGrid = letterGenerator.generateCharacter(cha, (int) size, space);
            int center = (int) (centerPopulator.getCenter(cha, (int) size));

            System.out.println("center = " + center);

            ImageLetter letter = new ImageLetter(new DatabaseCharacter(cha), x, (int) (lineY - center - (int) size + (int) size), letterGrid[0].length, letterGrid.length, -1D, null);
            letter.setValues(letterGrid);
            letter.setData(Color.BLACK);
            adding.add(letter);

            addBy = letterGrid[0].length + characterBetweenSpace;

            int finalAddBy = addBy;
            line.forEach(imageLetter -> imageLetter.setX(imageLetter.getX() + finalAddBy));

            System.out.println("x = " + x + " (" + cha + ")");
        }

        line.addAll(adding);

        scannedImage.getGrid().values().forEach(forLine -> {
            forLine.forEach(imageLetter -> {
                System.out.println(imageLetter.getLetter() + " (" + imageLetter.getX() + ", " + imageLetter.getY() + ")");
            });

            System.out.println("");
        });

        BufferedImage original = ImageIO.read(searchResult.getFile());

        BufferedImage bufferedImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(scannedImage, bufferedImage, searchResult.getFile());
        System.out.println("Writing to: " +  searchResult.getFile().getAbsolutePath());
        letterFileWriter.writeToFile();
    }
}

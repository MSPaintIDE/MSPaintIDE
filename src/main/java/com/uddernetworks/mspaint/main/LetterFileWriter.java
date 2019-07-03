package com.uddernetworks.mspaint.main;

import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.ScannedImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class LetterFileWriter {

    private ScannedImage scannedImage;
    private File writeFile;
    private BufferedImage image;

    public LetterFileWriter(ScannedImage scannedImage, BufferedImage image, File writeFile) {
        this.scannedImage = scannedImage;
        this.image = image;
        this.writeFile = writeFile;
    }

    public LetterFileWriter(ScannedImage scannedImage, File readFile, File writeFile) throws IOException {
        this.scannedImage = scannedImage;
        this.writeFile = writeFile;
        this.image = ImageIO.read(readFile);
    }

    public void writeToFile() throws IOException {
        AtomicInteger width = new AtomicInteger(this.image.getWidth());
        AtomicInteger height = new AtomicInteger(this.image.getHeight());
        scannedImage.getGrid().values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(imageLetter -> {
                    width.set(Math.max(imageLetter.getX() + imageLetter.getWidth() + 10, width.get()));
                    height.set(Math.max(imageLetter.getY() + imageLetter.getHeight() + 10, height.get()));
                });

        image = new BufferedImage(width.get(), height.get(), BufferedImage.TYPE_INT_ARGB);
        background(Color.WHITE);

        scannedImage.getGrid().values().forEach(line -> {
            line.forEach(imageLetter -> {
                if (imageLetter.getLetter() != ' ') {
                    writeLetterToFile(imageLetter);
                }
            });
        });

        if (writeFile != null) ImageIO.write(image, "png", writeFile);
    }

    public BufferedImage getImage() {
        return image;
    }

    private void writeLetterToFile(ImageLetter imageLetter, double[][] realValues) {
        for (int y = 0; y < realValues.length; y++) {
            for (int x = 0; x < realValues[0].length; x++) {
                var xyColor = (int) realValues[y][x];
                if (xyColor != 0) image.setRGB(imageLetter.getX() + x, imageLetter.getY() + y, xyColor);
            }
        }
    }

    private void writeLetterToFile(ImageLetter imageLetter) {
        var data = imageLetter.getData(double[][].class).orElseGet(() ->
                booleanToDoubleGrid(imageLetter.getValues(), imageLetter.getData(Color.class).map(Color::getRGB).orElse(Color.BLACK.getRGB())));

        writeLetterToFile(imageLetter, data);
    }

    private void background(Color color) {
        for (int y = 0; y < this.image.getHeight(); y++) {
            for (int x = 0; x < this.image.getWidth(); x++) {
                this.image.setRGB(x, y, color.getRGB());
            }
        }
    }

    private double[][] booleanToDoubleGrid(boolean[][] grid, double value) {
        var doubleGrid = new double[grid.length][];
        for (int i = 0; i < doubleGrid.length; i++) {
            var row = new double[grid[0].length];
            for (int i1 = 0; i1 < grid[i].length; i1++) {
                row[i1] = grid[i][i1] ? value : -1; // Isn't whitefailed to read
            }
            doubleGrid[i] = row;
        }

        return doubleGrid;
    }
}

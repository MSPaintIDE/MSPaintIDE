package com.uddernetworks.mspaint.main;

import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.ScannedImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
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
        System.out.println("Writing");
        ImageIO.write(this.image, "png", new File("E:\\MS Paint IDE Demos\\MS Paint IDE Demo\\highlighted\\shit.png"));
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
                if (imageLetter.getLetter() != ' ') writeLetterToFile(image, imageLetter);
            });
        });

        if (writeFile != null) ImageIO.write(image, "png", writeFile);
    }

    public BufferedImage getImage() {
        return image;
    }

    private void writeLetterToFile(BufferedImage image, ImageLetter imageLetter) {
        AtomicBoolean useSingleColor = new AtomicBoolean(false);
        var data = imageLetter.getData(double[][].class).orElseGet(() -> {
            useSingleColor.set(true);
            var grid = imageLetter.getValues();
            return booleanToDoubleGrid(grid, imageLetter.getData(Color.class).map(Color::getRGB).orElse(0));
        });

        for (int y = 0; y < imageLetter.getHeight(); y++) {
            for (int x = 0; x < imageLetter.getWidth(); x++) {
                var xyColor = (int) data[y][x];
                if (xyColor != 0) image.setRGB(imageLetter.getX() + x, imageLetter.getY() + y, xyColor);
            }
        }
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
                row[i1] = grid[i][i1] ? value : -1; // Isn't white
            }
            doubleGrid[i] = row;
        }

        return doubleGrid;
    }
}

package com.uddernetworks.mspaint.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LetterFileWriter {

    private List<List<Letter>> letterGrid;
    private File writeFile;
    private BufferedImage image;

    public LetterFileWriter(List<List<Letter>> letterGrid, BufferedImage image, File writeFile) {
        this.letterGrid = letterGrid;
        this.image = image;
        this.writeFile = writeFile;
    }

    public LetterFileWriter(List<List<Letter>> letterGrid, File readFile, File writeFile) throws IOException {
        this.letterGrid = letterGrid;
        this.writeFile = writeFile;
        this.image = ImageIO.read(readFile);
    }

    public void writeToFile(Map<String, BufferedImage> images) throws IOException {
        image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        background(Color.WHITE);

        for (List<Letter> row : letterGrid) {
            for (Letter letter : row) {
                if (letter == null || letter.getLetter().equals(" ")) continue;
                writeLetterToFile(image, images.get(letter.getLetter()), letter);
            }
        }

        if (writeFile != null) ImageIO.write(image, "png", writeFile);
    }

    public BufferedImage getImage() {
        return image;
    }

    private void writeLetterToFile(BufferedImage image, BufferedImage letterImage, Letter letter) {
        Color color = letter.getColor();
        letterImage = colorImage(letterImage, color.getRed(), color.getGreen(), color.getBlue());
        for (int y = 0; y < letterImage.getHeight(); y++) {
            for (int x = 0; x < letterImage.getWidth(); x++) {
//                System.out.println("(" + (letter.getX() + x) + ", " + (letter.getY() + y) + ")");
                image.setRGB(letter.getX() + x, letter.getY() + y, letterImage.getRGB(x, y));
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

    private static BufferedImage colorImage(BufferedImage image, int red, int green, int blue) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgba = image.getRGB(x, y);
                Color color = new Color(rgba, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int alpha = color.getAlpha();

                if (color.getRed() != 255 && color.getGreen() != 255 && color.getBlue() != 255) {
                    Color newColor = new Color(red, green, blue, alpha);
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        return image;
    }

}

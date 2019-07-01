package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.mspaint.ocr.FontData;
import com.uddernetworks.newocr.character.DatabaseCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

public class LetterGenerator {

    private static Logger LOGGER = LoggerFactory.getLogger(LetterGenerator.class);

    private Graphics2D graphics;
    private int lastSize = -1;
    private BufferedImage image;

    public LetterGenerator() {
        image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        clearImage();
    }

    private void clearImage() {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

    // TODO: Check this out
    public static double[][] trim(double[][] input) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int y = 0; y < input.length; y++) {
            for (int x = 0; x < input[0].length; x++) {
                if (input[y][x] != Color.WHITE.getRGB()) { // Isn't white
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
            }
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        var output = new double[height][width];
        for (int y = 0; y < output.length; y++) {
            var row = new double[output[y].length];
            Arrays.fill(row, Color.WHITE.getRGB()); // Set to white
            output[y] = row;
        }

        if (maxX == Integer.MIN_VALUE) return input;

        for (int y = 0; y < height; y++) {
            System.arraycopy(input[minY + y], minX, output[y], 0, width);
        }

        return output;
    }

    // TODO: Docs
    // Returns alpha values of characters, so default (no data) is 0
    public double[][] generateCharacter(char character, int size, FontData activeFont, DatabaseCharacter space) {
        clearImage();
        if (size != lastSize) {
            graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

            Font font = new Font(activeFont.getFontName(), Font.PLAIN, size);
            graphics.setFont(font);
            graphics.setColor(Color.BLACK);
            graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

        }

        if (character == ' ') {
            double ratio = space.getAvgWidth() / space.getAvgHeight();
            double width = ratio * (double) size;

            var grid = new double[size][(int) width];
            for (int i = 0; i < grid.length; i++) {
               var row = new double[(int) width];
                for (int i1 = 0; i1 < row.length; i1++) row[i1] = Color.WHITE.getRGB(); // Set to white

                grid[i] = row;
            }

            return grid;
        }

        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        graphics.drawString(character + "", 0, size);

        var values = createGrid(image);
        toGrid(image, values);

        return trim(values);
    }

    public static boolean[][] doubleToBooleanGrid(double[][] grid) {
        var boolGrid = new boolean[grid.length][];
        for (int i = 0; i < boolGrid.length; i++) {
            var row = new boolean[grid[0].length];
            for (int i1 = 0; i1 < grid[i].length; i1++) {
                row[i1] = grid[i][i1] != Color.WHITE.getRGB(); // Isn't white
            }
            boolGrid[i] = row;
        }

        return boolGrid;
    }

    public static double[][] createGrid(BufferedImage bufferedImage) {
        return new double[bufferedImage.getHeight()][bufferedImage.getWidth()];
    }

    public static void toGrid(BufferedImage input, double[][] values) {
        toGrid(input, values, null);
    }

    public static void toGrid(BufferedImage input, double[][] values, Color mask) {
        if (mask != null) input = createImage(input, mask, Color.WHITE);

        int arrX = 0;
        int arrY = 0;
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                values[arrY][arrX++] = input.getRGB(x, y);
            }

            arrX = 0;
            arrY++;
        }
    }

    // Thanks https://stackoverflow.com/a/30157899/3929546 :)
    private static BufferedImage createImage(BufferedImage original, Color... colors) {
        int w = original.getWidth();
        int h = original.getHeight();

        BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = gray.createGraphics();
        g2.drawImage(original, 0, 0, null);

        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];

        final int fade = (256 / (colors.length - 1));

        for (int i = 0; i < 256; ++i) {
            Color c0 = colors[(i / fade)];
            Color c1 = colors[(i / fade) + 1];

            float amt = (i % fade) / ((float) fade);

            r[i] = getChannel(amt, c0.getRed(),   c1.getRed());
            g[i] = getChannel(amt, c0.getGreen(), c1.getGreen());
            b[i] = getChannel(amt, c0.getBlue(),  c1.getBlue());
        }

        // (remap same pixels to new model)
        return new BufferedImage(
                new IndexColorModel(8, 256, r, g, b),
                gray.getRaster(), false, null);
    }

    private static byte getChannel(float amt, int ch0, int ch1) {
        return (byte) ((ch0 * (1 - amt)) + (ch1 * amt));
    }

}

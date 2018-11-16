package com.uddernetworks.mspaint.texteditor;

import com.uddernetworks.newocr.DatabaseCharacter;
import com.uddernetworks.newocr.OCRUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class LetterGenerator {

    private Graphics2D graphics;
    private int lastSize = -1;
    private BufferedImage image;

    public LetterGenerator() {
        image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();

        clearImage();

        RenderingHints rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rht);
    }

    private void clearImage() {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

    private boolean[][] trim(boolean[][] input) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int y = 0; y < input.length; y++) {
            for (int x = 0; x < input[0].length; x++) {
                if (input[y][x]) {
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                    maxX = Math.max(x + 2, maxX);
                    maxY = Math.max(y + 2, maxY);
                }
            }
        }

        int width = maxX - minX;
        int height = maxY - minY;

        boolean[][] output = new boolean[height][width];
        for (int y = 0; y < output.length; y++) {
            boolean[] row = new boolean[output[y].length];
            Arrays.fill(row, false);
            output[y] = row;
        }

        for (int y = 0; y < height; y++) {
            System.arraycopy(input[minY + y], minX, output[y], 0, width);
        }

        return output;
    }

    public boolean[][] generateCharacter(char character, int size, DatabaseCharacter space) {
        clearImage();
        if (size != lastSize) {
            Font font = new Font("Verdana", Font.PLAIN, size);
            graphics.setFont(font);
            graphics.setColor(Color.BLACK);
        }

        if (character == ' ') {
            double ratio = space.getAvgWidth() / space.getAvgHeight();
            double width = ratio * (double) size;

            boolean[][] grid = new boolean[size][(int) width];
            for (int i = 0; i < grid.length; i++) {
                boolean[] row = new boolean[(int) width];
                for (int i1 = 0; i1 < row.length; i1++) row[i1] = false;

                grid[i] = row;
            }

            return grid;
        }

        graphics.drawString(character + "", 0, size);

        OCRUtils.filter(image);

        boolean[][] values = OCRUtils.createGrid(image);
        OCRUtils.toGrid(image, values);

        return trim(values);
    }

}

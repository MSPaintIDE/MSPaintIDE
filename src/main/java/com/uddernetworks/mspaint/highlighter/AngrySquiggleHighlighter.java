package com.uddernetworks.mspaint.highlighter;

import com.uddernetworks.mspaint.main.Letter;

import javax.imageio.ImageIO;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AngrySquiggleHighlighter {

    private BufferedImage image;
    private int extraSquigglePadding = 0;
    private BufferedImage squiggleImage;
    private File highlightedFile;
    private List<List<Letter>> grid;
    private List<Diagnostic<? extends JavaFileObject>> errors;

    public AngrySquiggleHighlighter(BufferedImage image, int extraSquigglePadding, File squiggleFile, File highlightedFile, List<List<Letter>> grid, List<Diagnostic<? extends JavaFileObject>> errors) throws IOException {
        this.image = image;
        this.extraSquigglePadding = extraSquigglePadding;
        this.squiggleImage = ImageIO.read(squiggleFile);
        this.highlightedFile = highlightedFile;
        this.grid = grid;
        this.errors = errors;
    }

    public void highlightAngrySquiggles() throws IOException {
        for (Diagnostic<? extends JavaFileObject> error : errors) {
            int lineNumber = Long.valueOf(error.getLineNumber()).intValue();
            int columnNumber = Long.valueOf(error.getColumnNumber()).intValue();

            int[] locs = getLineAndLength(lineNumber, columnNumber);
            int xIndex = locs[0];
            int yIndex = locs[1];
            int length = locs[2];

            drawAngrySquiggle(xIndex, yIndex, length);
        }
    }

    private void drawAngrySquiggle(int squiggleX, int squiggleY, int length) throws IOException {
        for (int y = squiggleY; y < squiggleImage.getHeight() + squiggleY; y++) {
            for (int x = squiggleX; x < squiggleX + length; x++) {
                int squiggleImageX = (x - squiggleX) % squiggleImage.getWidth();
                int squiggleImageY = y - squiggleY;

                Color imageColor = new Color(image.getRGB(x, y));
                Color squiggleColor = new Color(squiggleImage.getRGB(squiggleImageX, squiggleImageY));
                Color combinedAlpha = new Color(squiggleColor.getRed(), squiggleColor.getGreen(), squiggleColor.getBlue(), imageColor.getAlpha());

                image.setRGB(x, y, combinedAlpha.getRGB());
            }
        }

        ImageIO.write(image, "png", highlightedFile);
    }


    private int[] getLineAndLength(int lineNumber, int columnNumber) {
        int[] ret = new int[3]; // X index, Y index, length

        Letter startLetter = null;
        Letter lastLetter = null;
        int index = 0;
        for (Letter letter : grid.get(lineNumber - 1)) {
            if (letter == null) continue;
            index++;

            if (columnNumber != -1) {
                if (index == columnNumber) {
                    ret[0] = letter.getX();
                    ret[1] = letter.getY() + 21;
                    ret[2] = columnNumber;
                    break;
                }
            }

            if (startLetter == null) {
                if (!letter.getLetter().equals(" ")) startLetter = letter;
            }

            lastLetter = letter;
        }

        if (columnNumber == -1) {
            ret[0] = startLetter.getX();
            ret[1] = startLetter.getY() + 21;
            ret[2] = lastLetter.getX() + lastLetter.getWidth() - startLetter.getX();
        } else if (index != columnNumber) {
            ret[0] = lastLetter.getX() + 7;
            ret[1] = lastLetter.getY() + 21;
            ret[2] = 6;
        }

        ret[0] = ret[0] - extraSquigglePadding;
        ret[2] = ret[2] + extraSquigglePadding * 2;

        return ret;
    }

}

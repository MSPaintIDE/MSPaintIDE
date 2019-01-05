package com.uddernetworks.mspaint.code.highlighter;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.LanguageError;
import com.uddernetworks.newocr.ScannedImage;
import com.uddernetworks.newocr.character.ImageLetter;
import org.apache.batik.transcoder.TranscoderException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AngrySquiggleHighlighter {

    private ImageClass imageClass;
    private BufferedImage image;
    private int extraSquigglePadding;
    private BufferedImage squiggleImage;
    private File highlightedFile;
    private ScannedImage scannedImage;
    private List<LanguageError> errors;

    public AngrySquiggleHighlighter(ImageClass imageClass, int extraSquigglePadding, File highlightedFile, ScannedImage scannedImage, List<LanguageError> errors) throws IOException, TranscoderException {
        this.imageClass = imageClass;
        this.image = imageClass.getImage();
        this.extraSquigglePadding = extraSquigglePadding;
        this.squiggleImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("angry_squiggle.png"));
        this.highlightedFile = highlightedFile;
        this.scannedImage = scannedImage;
        this.errors = errors;
    }

    public void highlightAngrySquiggles() throws IOException {
        for (LanguageError error : errors) {
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

        ImageLetter startLetter = null;
        ImageLetter lastLetter = null;
        int index = 0;
        for (ImageLetter colorWrapper : scannedImage.getLine(lineNumber - 1)) {
            if (colorWrapper == null) continue;
            index++;

            if (columnNumber != -1) {
                if (index == columnNumber) {
                    ret[0] = colorWrapper.getX();
                    ret[1] = colorWrapper.getY() + 21;
                    ret[2] = columnNumber;
                    break;
                }
            }

            if (startLetter == null) {
                if (colorWrapper.getLetter() != ' ') startLetter = colorWrapper;
            }

            lastLetter = colorWrapper;
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

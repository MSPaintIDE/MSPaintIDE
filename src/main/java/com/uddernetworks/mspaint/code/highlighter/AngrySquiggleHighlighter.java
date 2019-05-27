package com.uddernetworks.mspaint.code.highlighter;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.LanguageError;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.ScannedImage;
import com.uddernetworks.newocr.utils.ConversionUtils;
import org.apache.batik.transcoder.TranscoderException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AngrySquiggleHighlighter {

    private StartupLogic startupLogic;
    private ImageClass imageClass;
    private BufferedImage image;
    private double extraSquigglePaddingRatio;
    private File highlightedFile;
    private ScannedImage scannedImage;
    private List<LanguageError> errors;
    private BufferedImage squiggleImage; // The highlighter will use the size of the first character to calculate the persistent angry squiggle image

    public AngrySquiggleHighlighter(StartupLogic startupLogic, ImageClass imageClass, double extraSquigglePaddingRatio, File highlightedFile, ScannedImage scannedImage, List<LanguageError> errors) throws IOException, TranscoderException {
        this.startupLogic = startupLogic;
        this.imageClass = imageClass;
        this.image = imageClass.getImage();
        this.extraSquigglePaddingRatio = extraSquigglePaddingRatio;
        this.highlightedFile = highlightedFile;
        this.scannedImage = scannedImage;
        this.errors = errors;
    }

    public void highlightAngrySquiggles() throws IOException, TranscoderException {
        for (LanguageError error : errors) {
            int lineNumber = Long.valueOf(error.getLineNumber()).intValue() - 1;
            int columnNumber = Long.valueOf(error.getColumnNumber()).intValue() - 1;

            getLineAndLength(lineNumber, columnNumber);
        }
    }

    private void drawAngrySquiggle(int squiggleX, int squiggleY, int length) throws IOException {
        for (int y = squiggleY; y < squiggleImage.getHeight() + squiggleY; y++) {
            for (int x = squiggleX; x < squiggleX + length; x++) {
                if (!isInBounds(x, y)) continue;
                int squiggleImageX = (x - squiggleX) % squiggleImage.getWidth();
                int squiggleImageY = y - squiggleY;

                Color squiggleColor = new Color(squiggleImage.getRGB(squiggleImageX, squiggleImageY), true);

                if (squiggleColor.getAlpha() == 255) image.setRGB(x, y, squiggleColor.getRGB());
            }
        }

        ImageIO.write(image, "png", highlightedFile);
    }

    private boolean isInBounds(int x, int y) {
        return this.squiggleImage.getWidth() < x
                && 0 <= x
                && this.squiggleImage.getHeight() < y
                && 0 <= y;
    }

    private void getLineAndLength(int lineNumber, int columnNumber) throws IOException, TranscoderException {
        var actions = this.startupLogic.getOCRManager().getActions();
        int xIndex;
        int yIndex;
        int length;
        int fontSize = 0;

        List<ImageLetter> line = this.scannedImage.getLine(lineNumber);
        ImageLetter first = line.get(0);
        ImageLetter last = line.get(line.size() - 1);
        ImageLetter calcXY;

        int i = 0;
        Optional<ImageLetter> letterOptional;
        while ((letterOptional = this.scannedImage.letterAt(i++)).isPresent()) {
            var letter = letterOptional.get();
            if (letter.getLetter() == ' ') continue;
            fontSize = (int) actions.getFontSize(letter).getAsDouble();
        }

        int extraSquigglePadding = 0;

        if (columnNumber == -1) {
            calcXY = first;

            length = last.getX() + last.getWidth() - first.getX();
        } else {
            ImageLetter columnLetter = line.get(Math.min(columnNumber, line.size() - 1)); // Need to get BEFORE
            System.out.println("Column letter info: " + columnLetter.getLetter()  +" width: " + columnLetter.getWidth());
            calcXY = columnLetter;
            length = columnLetter.getWidth();
            extraSquigglePadding = (int) Math.round(this.extraSquigglePaddingRatio * length);
        }

        xIndex = calcXY.getX() + (int) Math.round(calcXY.getWidth() / 2D);
        yIndex = calcXY.getY() + calcXY.getHeight();

        fontSize = ConversionUtils.pointToPixel(fontSize); // fontSize is now in pixels

        length = length + extraSquigglePadding * 2;

        if (this.squiggleImage == null) {
            AngrySquiggleGenerator angrySquiggleGenerator = new AngrySquiggleGenerator(fontSize);
            this.squiggleImage = angrySquiggleGenerator.getGeneratedPNG();
        }

        length = getRoundedSquiggleLength(length);

        drawAngrySquiggle((int) Math.round(xIndex - (length / 2D)), (int) Math.round(yIndex + this.squiggleImage.getHeight() / 2D), length);
    }

    private int getRoundedSquiggleLength(int originalLength) {
        int finalLength = originalLength;
        int imageWidth = this.squiggleImage.getWidth();

        int extra = originalLength % imageWidth;
        finalLength -= extra;
        if (extra > 0) finalLength += imageWidth;

        return finalLength;
    }

}

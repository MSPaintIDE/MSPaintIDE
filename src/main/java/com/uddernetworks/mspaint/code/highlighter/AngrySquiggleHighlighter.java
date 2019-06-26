package com.uddernetworks.mspaint.code.highlighter;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.newocr.character.ImageLetter;
import com.uddernetworks.newocr.recognition.ScannedImage;
import com.uddernetworks.newocr.utils.ConversionUtils;
import org.apache.batik.transcoder.TranscoderException;
import org.eclipse.lsp4j.Diagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AngrySquiggleHighlighter {

    private static Logger LOGGER = LoggerFactory.getLogger(AngrySquiggleHighlighter.class);

    private StartupLogic startupLogic;
    private ImageClass imageClass;
    private BufferedImage image;
    private double extraSquigglePaddingRatio;
    private File highlightedFile;
    private ScannedImage scannedImage;
    private List<Diagnostic> diagnostics;
    private BufferedImage squiggleImage; // The highlighter will use the size of the first character to calculate the persistent angry squiggle image

    public AngrySquiggleHighlighter(StartupLogic startupLogic, ImageClass imageClass, double extraSquigglePaddingRatio, File highlightedFile, ScannedImage scannedImage, List<Diagnostic> diagnostics) {
        this.startupLogic = startupLogic;
        this.imageClass = imageClass;
        this.image = imageClass.getImage();
        this.extraSquigglePaddingRatio = extraSquigglePaddingRatio;
        this.highlightedFile = highlightedFile;
        this.scannedImage = scannedImage;
        this.diagnostics = diagnostics;
    }

    public void highlightAngrySquiggles() throws IOException, TranscoderException {
        LOGGER.info("Starting highlight");
        for (Diagnostic diagnostic : diagnostics) {
            var range = diagnostic.getRange();
            var start = range.getStart();
            var end = range.getEnd();
            int startLine = start.getLine();
            int startColumn = start.getCharacter();

            var endLine = end.getLine();
            var endColumn = end.getCharacter();

            if (endLine != startLine) {
                var numOfFullLines = endLine - startLine - 1;
                for (int fullLine = 0; fullLine < numOfFullLines; fullLine++) {
                    LOGGER.info("Highlight full {}", fullLine);
                    getLineAndLength(startLine, startColumn, Integer.MAX_VALUE);
                }

                getLineAndLength(startLine, startColumn, Integer.MAX_VALUE);
                getLineAndLength(endLine, endColumn, Integer.MAX_VALUE);
            } else {
                getLineAndLength(startLine, startColumn, endColumn - startColumn);
            }
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

    private void getLineAndLength(int lineNumber, int columnNumber, int length) throws IOException, TranscoderException {
        var actions = this.startupLogic.getOCRManager().getActions();
        int xIndex;
        int yIndex;
        int pixelLength;
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
            pixelLength = last.getX() + last.getWidth() - first.getX();
        } else {
            var startIndex = Math.min(columnNumber, line.size() - 1); // Need to get BEFORE
            ImageLetter firstLetter = line.get(startIndex);
            System.out.println("Column letter info: " + firstLetter.getLetter()  +" width: " + firstLetter.getWidth());
            calcXY = firstLetter;

            LOGGER.info("Length = {} plus {} is {}", length, startIndex, startIndex + length);
            var lastLetterIndex = 0;
            if (length == Integer.MAX_VALUE) {
                lastLetterIndex = line.size() - 1;
            } else {
                lastLetterIndex = Math.min(startIndex + length, line.size() - 1);
            }
            var lastLetter = line.get(lastLetterIndex);

            pixelLength = lastLetter.getX() - firstLetter.getX() + lastLetter.getWidth();
            extraSquigglePadding = (int) Math.round(this.extraSquigglePaddingRatio * pixelLength);
        }

        xIndex = calcXY.getX() + (int) Math.round(calcXY.getWidth() / 2D);
        yIndex = calcXY.getY() + calcXY.getHeight();

        fontSize = ConversionUtils.pointToPixel(fontSize); // fontSize is now in pixels

        pixelLength = pixelLength + extraSquigglePadding * 2;

        if (this.squiggleImage == null) {
            AngrySquiggleGenerator angrySquiggleGenerator = new AngrySquiggleGenerator(fontSize);
            this.squiggleImage = angrySquiggleGenerator.getGeneratedPNG();
        }

        pixelLength = getRoundedSquiggleLength(pixelLength);

        drawAngrySquiggle((int) Math.round(xIndex - (pixelLength / 2D)), (int) Math.round(yIndex + this.squiggleImage.getHeight() / 2D), pixelLength);
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

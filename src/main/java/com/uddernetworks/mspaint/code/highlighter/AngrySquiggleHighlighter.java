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
import java.util.concurrent.ExecutionException;

public class AngrySquiggleHighlighter {

    private StartupLogic startupLogic;
    private ImageClass imageClass;
    private BufferedImage image;
    private int extraSquigglePadding;
    private File highlightedFile;
    private ScannedImage scannedImage;
    private List<LanguageError> errors;
    private BufferedImage squiggleImage; // The highlighter will use the size of the first character to calculate the persistent angry squiggle image

    public AngrySquiggleHighlighter(StartupLogic startupLogic, ImageClass imageClass, int extraSquigglePadding, File highlightedFile, ScannedImage scannedImage, List<LanguageError> errors) throws IOException, TranscoderException {
        this.startupLogic = startupLogic;
        this.imageClass = imageClass;
        this.image = imageClass.getImage();
        this.extraSquigglePadding = extraSquigglePadding;
        this.highlightedFile = highlightedFile;
        this.scannedImage = scannedImage;
        this.errors = errors;
    }

    public void highlightAngrySquiggles() throws IOException, TranscoderException, ExecutionException, InterruptedException {
        for (LanguageError error : errors) {
            int lineNumber = Long.valueOf(error.getLineNumber()).intValue() - 1;
            int columnNumber = Long.valueOf(error.getColumnNumber()).intValue() - 1;

            getLineAndLength(lineNumber, columnNumber);
        }
    }

    private void drawAngrySquiggle(int squiggleX, int squiggleY, int length) throws IOException {
        for (int y = squiggleY; y < squiggleImage.getHeight() + squiggleY; y++) {
            for (int x = squiggleX; x < squiggleX + length; x++) {
                int squiggleImageX = (x - squiggleX) % squiggleImage.getWidth();
                int squiggleImageY = y - squiggleY;

                Color squiggleColor = new Color(squiggleImage.getRGB(squiggleImageX, squiggleImageY), true);

                if (squiggleColor.getAlpha() == 255) image.setRGB(x, y, squiggleColor.getRGB());
            }
        }

        ImageIO.write(image, "png", highlightedFile);
    }


    private void getLineAndLength(int lineNumber, int columnNumber) throws IOException, TranscoderException {
        var actions = this.startupLogic.getOCRManager().getActions();
        int xIndex;
        int yIndex;
        int length;
        int fontSize;

        List<ImageLetter> line = this.scannedImage.getLine(lineNumber);
        ImageLetter first = line.get(0);
        ImageLetter last = line.get(line.size() - 1);
        ImageLetter calcXY;
        if (columnNumber == -1) {
            calcXY = first;

            length = last.getX() + last.getWidth() - first.getX();

            // TODO: May need conversion?
            fontSize = (int) actions.getFontSize(first).getAsDouble();
        } else {
            ImageLetter columnLetter = line.get(columnNumber - 1); // Need to get BEFORE
            calcXY = columnLetter;
            length = columnLetter.getWidth();

            fontSize = (int) actions.getFontSize(columnLetter).getAsDouble();
        }

        xIndex = calcXY.getX() + calcXY.getWidth();
        yIndex = calcXY.getY() + calcXY.getHeight();

        fontSize = ConversionUtils.pointToPixel(fontSize); // fontSize is now in pixels

        xIndex -= extraSquigglePadding;
        length = length + extraSquigglePadding * 2;

        if (this.squiggleImage == null) {
            AngrySquiggleGenerator angrySquiggleGenerator = new AngrySquiggleGenerator(fontSize);
            this.squiggleImage = angrySquiggleGenerator.getGeneratedPNG();
        }

        length = getRoundedSquiggleLength(length);

        drawAngrySquiggle(xIndex, yIndex + this.squiggleImage.getHeight(), length);
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

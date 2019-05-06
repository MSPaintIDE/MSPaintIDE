package com.uddernetworks.mspaint.splash;

import com.uddernetworks.newocr.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SplashGen {

    private static Logger LOGGER = LoggerFactory.getLogger(SplashGen.class);

    public static void main(String[] args) {
        var font = args.length == 2 ? args[0] : "Comic Sans MS";
        var sizeInPts = args.length == 2 ? Integer.valueOf(args[0]) : 12;

        LOGGER.info("Generating splash text images...");

        var splashDir = new File("splash");
        splashDir.mkdirs();
        Arrays.stream(SplashMessage.values()).forEach(splashMessage -> {
            try {
                ImageIO.write(generateImage(splashMessage.getMessage(), font, sizeInPts), "png", new File(splashDir, splashMessage.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        LOGGER.info("Finished generating " + SplashMessage.values().length + " splash text images!");
    }

    private static BufferedImage generateImage(String message, String fontFamily, int size) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        var pxSize = ConversionUtils.pointToPixel(size);

        Font font = new Font(fontFamily, Font.PLAIN, pxSize);
        graphics.setFont(font);

        image = new BufferedImage(Math.max(graphics.getFontMetrics().stringWidth(message), 1), pxSize, BufferedImage.TYPE_INT_ARGB);

        graphics = image.createGraphics();
        graphics.setFont(font);

        graphics.setPaint(Color.BLACK);
        graphics.drawString(message, 0, size);

        return image;
    }

}

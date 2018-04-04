package com.uddernetworks.mspaint.main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.*;

public class ImageUtil {

    public static BufferedImage blackAndWhite(BufferedImage image) {
        BufferedImage wImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                int r = col.getRed();
                int g = col.getGreen();
                int b = col.getBlue();
                int alpha = col.getAlpha();

                if (shouldBeBlack(r, g, b)) {
                    Color newColor = new Color(0, 0, 0, alpha);
                    wImage.setRGB(x, y, newColor.getRGB());
                } else {
                    Color newColor = new Color(255, 255, 255, alpha);
                    wImage.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        return wImage;
    }

    public static boolean shouldBeBlack(int red, int green, int blue) {
        double luminence = (0.2126 * red + 0.7152 * green + 0.0722 * blue);
        return luminence < 233;
    }

    public static boolean equals(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) return false;
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) return false;
            }
        }
        return true;

//        double similarity = getDifferenceBetween(image1, image2);
//
//        if (similarity < 1) return false;
//
//        System.out.println("Probably a match, similarity = " + similarity);
//
//        return true;
    }

    // Returns 1 for exact match, 0 for none matching
    public static double getDifferenceBetween(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) return 0;
        double totalPixels = image1.getWidth() * image2.getHeight();
        double same = 0;

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                if (image1.getRGB(x, y) == image2.getRGB(x, y)) same++;
            }
        }

        return same / totalPixels;
    }
}

package com.uddernetworks.mspaint.main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

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

                if (r != 255 && g != 255 && b != 255) {
                    Color newColor = new Color(0, 0, 0, alpha);
                    wImage.setRGB(x, y, newColor.getRGB());
                } else {
                    wImage.setRGB(x, y, col.getRGB());
                }
            }
        }

        return wImage;
    }

    public static boolean equals(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) return false;
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) return false;
            }
        }
        return true;
    }
}

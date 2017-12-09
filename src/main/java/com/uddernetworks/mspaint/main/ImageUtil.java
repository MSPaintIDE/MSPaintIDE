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

                if (shouldBeBlack(r, g, b)) {
                    wImage.setRGB(x, y, new Color(0, 0, 0).getRGB());
                } else {
                    wImage.setRGB(x, y, new Color(255, 255, 255).getRGB());
                }
            }
        }

        return wImage;
    }

    public static BufferedImage trimWhitespace(BufferedImage image) {
        int leftTrim = 0;
        int rightTrim = 0;

        int moveFromLeft = (image.getWidth() / 2) + (((image.getWidth() / 2) % 2 != 0) ? 1 : 0);
        int moveFromRight = (image.getWidth() / 2);

        for (int i = 0; i < moveFromLeft; i++) {
            if (columnIsWhite(image, i)) {
                leftTrim++;
            }
        }

        for (int i = moveFromRight - 1; i >= 0; i--) {
            if (columnIsWhite(image, i)) {
                rightTrim++;
            }
        }

        return image.getSubimage(leftTrim, 0, image.getWidth() - leftTrim - rightTrim, image.getHeight());
    }

    private static boolean columnIsWhite(BufferedImage image, int column) {
        for (int y = 0; y < image.getHeight(); y++) {
            int rgba = image.getRGB(column, y);
            Color col = new Color(rgba, true);
            int r = col.getRed();
            int g = col.getGreen();
            int b = col.getBlue();

            if (r != 255 || g != 255 || b != 255) {
                return false;
            }
        }

        return true;
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

    private static boolean shouldBeBlack(int r, int g, int b) {
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return luminance < 128;
//        return r != 0 || g != 0 || b != 0;

//        if (r != 255 || g != 255 || b != 255) {
//            return true;
//        }
//        return false;
    }

}

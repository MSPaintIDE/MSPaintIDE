package com.uddernetworks.mspaint.main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BitImage {

    List<List<Boolean>> bits = new ArrayList<>();

    public BitImage(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            List<Boolean> row = new ArrayList<>();
            for (int x = 0; x < image.getWidth(); x++) {
                int rgba = image.getRGB(x, y);
                Color col = new Color(rgba, true);
                int r = col.getRed();

                row.add(r == 0); // True for black, False for white
            }
            bits.add(row);
        }
    }

}

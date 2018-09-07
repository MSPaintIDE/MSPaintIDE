package com.uddernetworks.mspaint.main;

import java.awt.image.BufferedImage;

public class Probe {

    private BufferedImage image;
    private BufferedImage probe;

    public Probe(BufferedImage image, BufferedImage probe) {
        this.image = image;
        this.probe = probe;
    }

    public int sendInProbe() {
        int currentX;
        int currentY = 0;

        while (currentY + probe.getHeight() <= image.getHeight()) {
            currentX = 0;
            while (currentX + probe.getWidth() <= image.getWidth()) {
                BufferedImage subImage = image.getSubimage(currentX, currentY, probe.getWidth(), probe.getHeight());

                if (ImageUtil.equals(subImage, probe)) {
                    return currentY;
//                    yVals.add(currentY);
                }
                currentX++;
            }
            currentY++;
        }

        return 0;
    }

}

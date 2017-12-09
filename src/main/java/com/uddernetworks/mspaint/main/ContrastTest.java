package com.uddernetworks.mspaint.main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ContrastTest {

    public static void main(String[] args) {
        File readImage = new File("E:\\MSPaintIDE\\letters\\c.png");
        File writeImage = new File("E:\\MSPaintIDE\\c_contrast.png");
        File writeImage2 = new File("E:\\MSPaintIDE\\c_trimmed.png");

        try {
            BufferedImage image = ImageIO.read(readImage);

//            BufferedImage wImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_ARGB);

            BufferedImage wImage = ImageUtil.blackAndWhite(image);

            ImageIO.write(wImage, "png", writeImage);

            wImage = ImageUtil.trimWhitespace(wImage);

            ImageIO.write(wImage, "png", writeImage2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class TrainGenerator {
    private String trainString = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghjiklmnopqrstuvwxyz{|}~W W";
    private int UPPER_FONT_BOUND;
    private int LOWER_FONT_BOUND;

    public void generate(File file, Runnable callback) {
        CompletableFuture.runAsync(() -> {
            UPPER_FONT_BOUND = SettingsManager.getSetting(Setting.TRAIN_UPPER_BOUND, Integer.class);
            LOWER_FONT_BOUND = SettingsManager.getSetting(Setting.TRAIN_LOWER_BOUND, Integer.class);

            BufferedImage image = new BufferedImage(1500, 500, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();

            RenderingHints rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHints(rht);

            Font font = new Font("Verdana", Font.PLAIN, UPPER_FONT_BOUND + 2);
            graphics.setFont(font);

            int newHeight = 11;

            int size2 = UPPER_FONT_BOUND;
            for (int i = 0; i < UPPER_FONT_BOUND - LOWER_FONT_BOUND; i++) {
                newHeight += size2 + 11;
                size2--;
            }

            image = new BufferedImage(graphics.getFontMetrics().stringWidth(trainString) + 50, newHeight, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }

            graphics = image.createGraphics();

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHints(rh);

            int size = UPPER_FONT_BOUND;
            int offset = UPPER_FONT_BOUND;
            for (int i = 0; i < UPPER_FONT_BOUND - LOWER_FONT_BOUND; i++) {
                drawLine(graphics, trainString, offset, size);
                offset += size + 10;
                size--;
            }

            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenRun(callback);
    }

    private void drawLine(Graphics2D drawTo, String line, int yOffset, int size) {
        Font font = new Font("Verdana", Font.PLAIN, size);
        drawTo.setFont(font);
        drawTo.setPaint(Color.BLACK);

        drawTo.drawString(line, 10, yOffset);
    }
}

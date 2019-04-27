package com.uddernetworks.mspaint.imagestreams;

import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageOutputStream extends OutputStream {
    private StringBuilder string = new StringBuilder();
    private File location;
    private Graphics2D graphics;
    private int width;
    private int minHeight;
    private Color color;
    private Color background;

    public ImageOutputStream(File location, int width) {
        this.location = location;

        this.width = width;
        this.minHeight = 200;

        this.color = Color.BLACK;
        this.background = Color.WHITE;
    }

    @Override
    public void write(int b) {
        string.append((char) b);
    }

    public void saveImage() {
        BufferedImage image = new BufferedImage(width, minHeight, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();

        String message = string.toString();

        RenderingHints rht = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rht);

        Font fontt = new Font(SettingsManager.getSetting(Setting.ACTIVE_FONT, String.class), Font.PLAIN, 16);
        graphics.setFont(fontt);

        List<String> linesList = new ArrayList<>();

        String[] lines = message.split("\n");
        for (String line1 : lines) {
            String[] innerLines = breakStringUp(line1).split("\n");
            linesList.addAll(Arrays.asList(innerLines));
        }

        int newHeight = linesList.size() * 20;

        var height = Math.max(newHeight, minHeight);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = image.createGraphics();

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(rh);

        Font font = new Font(SettingsManager.getSetting(Setting.ACTIVE_FONT, String.class), Font.PLAIN, 16);
        graphics.setFont(font);
        graphics.setPaint(this.background);
        graphics.fillRect(0, 0, this.width, height);
        graphics.setPaint(this.color);

        for (int i = 0; i < linesList.size(); i++) {
            graphics.drawString(linesList.get(i), 10, 20 + (i * 20));
        }

        try {
            ImageIO.write(image, "png", location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeColor(Color color) {
        this.color = color;
    }

    public void changeBackground(Color background) {
        this.background = background;
    }

    private String breakStringUp(String message) {
        FontMetrics fontMetrics = graphics.getFontMetrics();

        String[] words = message.split(" ");

        StringBuilder ret = new StringBuilder();

        int currentWidth = 0;
        for (String word : words) {
            int currWordWidth = fontMetrics.stringWidth(word + " ");

            if (currentWidth + currWordWidth + 20 > width) {
                ret.append("\n");
                currentWidth = 0;

                if (currWordWidth + 20 > width) {
                    ret.append(breakWordUp(word));
                } else {
                    ret.append(word).append(" ");
                }
            } else {
                currentWidth += currWordWidth;
                ret.append(word).append(" ");
            }
        }

        return ret.toString();
    }

    private String breakWordUp(String word) {
        FontMetrics fontMetrics = graphics.getFontMetrics();

        char[] chars = word.toCharArray();

        StringBuilder ret = new StringBuilder();

        int currentWidth = 0;
        for (char cha : chars) {
            int currWordWidth = fontMetrics.stringWidth(Character.toString(cha));

            if (currentWidth + currWordWidth + 20 > width) {
                ret.append("\n");
                currentWidth = 0;
                ret.append(cha);
            } else {
                currentWidth += currWordWidth;
                ret.append(cha);
            }
        }

        return ret.toString();
    }
}
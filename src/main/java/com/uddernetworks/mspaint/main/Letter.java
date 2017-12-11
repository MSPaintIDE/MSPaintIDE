package com.uddernetworks.mspaint.main;

import java.awt.*;
import java.io.Serializable;

public class Letter implements Serializable {

    private static final long serialVersionUID = 2L;
    private String letter;
    private int width;
    private int height;
    private int x;
    private int y;
    private Color color = new Color(0, 0, 0);

    public Letter(String letter, int width, int height, int x, int y) {
        this.letter = letter;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public String getLetter() {
        return letter;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return letter;
    }
}

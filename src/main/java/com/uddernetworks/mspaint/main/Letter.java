package com.uddernetworks.mspaint.main;

import java.io.Serializable;

public class Letter implements Serializable {

    private static final long serialVersionUID = 2L;
    private String letter;
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean hangsDown;
    private boolean placeholder;

    public Letter() {
        this.placeholder = true;
    }

    public Letter(String letter, int width, int height, int x, int y, boolean hangsDown) {
        this.letter = letter;
        this.width = width;
        this.height = height;
        this.x = x;
        this.hangsDown = hangsDown;
        this.placeholder = false;

//        if (height != 21) {
//            this.y = y - (21 - height) - (hangsDown ? 4 : 0);
//        } else {
            this.y = y;
//        }
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

    public boolean hangsDown() {
        return hangsDown;
    }

    public boolean isPlaceholder() {
        return placeholder;
    }

    @Override
    public String toString() {
        return letter;
    }
}

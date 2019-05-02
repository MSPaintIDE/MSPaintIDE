package com.uddernetworks.mspaint.gui.fonts;

public class OCRFont {

    private boolean selected;
    private int index;
    private String name;
    private String path;

    public OCRFont(int index, String name, String path) {
        this.index = index;
        this.name = name;
        this.path = path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

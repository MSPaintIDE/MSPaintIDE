package com.uddernetworks.mspaint.gui.fonts;

public class OCRFont {

    private boolean selected;
    private String name;
    private String path;

    public OCRFont(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public OCRFont(String name, String path, boolean selected) {
        this.name = name;
        this.path = path;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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

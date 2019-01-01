package com.uddernetworks.mspaint.main.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class MaterialMenuItem extends MenuItem {

    private Label label;
    private String customText;
    private StringProperty clickLabel;

    public String getCustomText() {
        return customText;
    }

    public MaterialMenuItem() {
        label = new Label();

        label.setPrefWidth(180);
        label.setPadding(new Insets(0, 0, 0, 5));

        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphicTextGap(0);
        setGraphic(label);
    }

    public void initialize() {
        label.setText(getText());
        this.customText = getText();
        setText(null);
    }

    public final void setClickLabel(String clickLabel) {
        clickLabelProperty().set(clickLabel);
    }

    public final String getClickLabel() {
        return clickLabel == null ? null : clickLabel.get();
    }

    public final StringProperty clickLabelProperty() {
        if (clickLabel == null) {
            clickLabel = new SimpleStringProperty(this, null);
        }

        return clickLabel;
    }

    @Override
    public String toString() {
        return "MaterialMenuItem[" + customText + "]";
    }
}
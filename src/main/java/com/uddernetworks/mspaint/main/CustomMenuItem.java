package com.uddernetworks.mspaint.main;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class CustomMenuItem extends MenuItem {

    public CustomMenuItem() {
        Label label = new Label();

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                label.setText(newValue);
                setText(null);
            }
        });

        label.setPrefWidth(180);
        label.setPadding(new Insets(0, 0, 0, 5));

        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setGraphicTextGap(0);
        setGraphic(label);
    }
}
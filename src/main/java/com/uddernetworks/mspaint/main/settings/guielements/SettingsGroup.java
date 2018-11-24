package com.uddernetworks.mspaint.main.settings.guielements;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class SettingsGroup extends StackPane {
    private Label title = new Label();
    private StackPane contentPane = new StackPane();
    private Node content;

    public SettingsGroup() {
        title.setText("Default");
        title.getStyleClass().add("settings-group-title");
        title.getStyleClass().add("theme-text");
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        getStyleClass().add("settings-group-border");
        getChildren().addAll(title, contentPane);
    }

    public void setContent(Node content) {
        content.getStyleClass().add("settings-group-content");
        contentPane.getChildren().add(content);
    }


    public Node getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title.setText(" " + title + " ");
    }

    public String getTitle() {
        return title.getText();
    }
}

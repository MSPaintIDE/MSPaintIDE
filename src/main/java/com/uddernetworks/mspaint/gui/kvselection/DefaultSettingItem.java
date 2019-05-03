package com.uddernetworks.mspaint.gui.kvselection;

import com.uddernetworks.mspaint.gui.SettingItem;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class DefaultSettingItem implements SettingItem {

    private String name;
    private String file;

    public DefaultSettingItem() {}

    public DefaultSettingItem(String name, String file) {
        this.name = name;
        this.file = file;
    }

    @Override
    public Pane getPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.file));
        Parent root = loader.load();
        Node node = root.lookup("*");

        if (!(node instanceof Pane)) throw new LoadException("Root element of " + this.file + " not a pane!");

        return (Pane) node;
    }

    @Override
    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return name;
    }
}

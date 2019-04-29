package com.uddernetworks.mspaint.gui;

import javafx.scene.layout.Pane;

import java.io.IOException;

public interface SettingItem {
    Pane getPane() throws IOException;

    String getFile();

    @Override
    String toString();
}

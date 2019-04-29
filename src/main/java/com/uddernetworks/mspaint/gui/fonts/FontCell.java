package com.uddernetworks.mspaint.gui.fonts;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.MainGUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Collections;

public class FontCell extends ListCell<OCRFont> {

    @FXML
    private AnchorPane anchor;

    @FXML
    private JFXRadioButton using;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField path;

    private FXMLLoader fxmlLoader;
    private MainGUI mainGUI;
    private ToggleGroup group;

    public FontCell(MainGUI mainGUI, ToggleGroup group) {
        this.mainGUI = mainGUI;
        this.group = group;
    }

    @Override
    protected void updateItem(OCRFont item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {

        } else {
            if (fxmlLoader == null) {
                setItem(item);
                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("file/ocr/font.fxml"));
                fxmlLoader.setRoot(this);
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                using.setToggleGroup(group);
                using.setUserData(this);

                this.mainGUI.getThemeManager().onDarkThemeChange(anchor, Collections.emptyMap());
            }

            name.setText(item.getName());
            path.setText(item.getPath());
        }
    }

}

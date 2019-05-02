package com.uddernetworks.mspaint.gui.fonts;

import com.jfoenix.controls.JFXButton;
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
import java.util.function.Consumer;

public class FontCell extends ListCell<OCRFont> {

    @FXML
    private AnchorPane anchor;

    @FXML
    private JFXRadioButton using;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField path;

    @FXML
    private JFXButton removeEntry;

    private FXMLLoader fxmlLoader;
    private MainGUI mainGUI;
    private ToggleGroup group;
    private Consumer<FontCell> onInit;

    public FontCell(MainGUI mainGUI, ToggleGroup group, Consumer<FontCell> onInit) {
        this.mainGUI = mainGUI;
        this.group = group;
        this.onInit = onInit;
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

                this.onInit.accept(this);
            }

            name.setText(item.getName());
            path.setText(item.getPath());
        }
    }

    public JFXTextField getName() {
        return name;
    }

    public JFXTextField getPath() {
        return path;
    }

    public JFXButton getRemoveEntry() {
        return removeEntry;
    }
}

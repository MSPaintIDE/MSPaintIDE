package com.uddernetworks.mspaint.gui.kvselection;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.function.Consumer;

public class KVCell extends ListCell<KVData> {

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
    private ThemeManager.ThemeChanger themeChanger;
    private final String keyPlaceholder;
    private final String valuePlaceholder;
    private Consumer<KVCell> onInit;

    public KVCell(MainGUI mainGUI, ThemeManager.ThemeChanger themeChanger, String keyPlaceholder, String valuePlaceholder, Consumer<KVCell> onInit) {
        this.mainGUI = mainGUI;
        this.themeChanger = themeChanger;
        this.keyPlaceholder = keyPlaceholder;
        this.valuePlaceholder = valuePlaceholder;
        this.onInit = onInit;

        getStyleClass().add("gridpane-theme");
        if (SettingsManager.getSetting(Setting.DARK_THEME, Boolean.class)) {
            getStyleClass().add("gridpane-theme-dark");
        }
    }

    @Override
    protected void updateItem(KVData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                setItem(item);
                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("file/ocr/kvRow.fxml"));
                fxmlLoader.setRoot(this);
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                using.setUserData(this);

                this.onInit.accept(this);
            }

            using.setSelected(getItem().isSelected());

            name.setPromptText(this.keyPlaceholder);
            path.setPromptText(this.valuePlaceholder);

            name.setText(item.getName());
            path.setText(item.getPath());

            setText(null);
            setGraphic(anchor);
        }

        this.themeChanger.update();
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

    public JFXRadioButton getRadio() {
        return this.using;
    }
}

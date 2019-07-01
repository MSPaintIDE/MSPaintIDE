package com.uddernetworks.mspaint.gui;

import com.jfoenix.controls.JFXButton;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LSPSettingItem extends Stage implements SettingItem, Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(LSPSettingItem.class);

    @FXML
    private JFXButton downloadRuntime;

    @FXML
    private JFXButton downloadLSP;

    private String name;
    private String file;
    private MainGUI mainGUI;
    private ThemeManager.ThemeChanger themeChanger;

    public LSPSettingItem(String name, String file, MainGUI mainGUI) {
        this.name = name;
        this.file = file;
        this.mainGUI = mainGUI;
    }

    @Override
    public Pane getPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.file));
        loader.setController(this);
        Parent root = loader.load();
        Node node = root.lookup("*");

        if (!(node instanceof Pane)) throw new LoadException("Root element of " + this.file + " not a pane!");

//        this.themeChanger = this.mainGUI.getThemeManager().onDarkThemeChange((Pane) node, Map.of(
//                "#fontSelect", "dark",
//                ".remove-entry", "remove-entry-white"
//        ));

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.downloadRuntime.setDisableVisualFocus(true);

        this.downloadLSP.setOnAction(event -> {
            var lang = this.mainGUI.getStartupLogic().getCurrentLanguage();
            if (lang.installLSP()) {
                LOGGER.info("Install was successful!");
            } else {
                LOGGER.info("Install was NOT successful");
            }
        });
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

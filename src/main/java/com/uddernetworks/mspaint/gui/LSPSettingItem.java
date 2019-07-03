package com.uddernetworks.mspaint.gui;

import com.jfoenix.controls.JFXButton;
import com.uddernetworks.mspaint.gui.elements.SettingsGroup;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.util.Browse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LSPSettingItem extends Stage implements SettingItem, Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(LSPSettingItem.class);

    @FXML
    private VBox container;

    private String name;
    private String file;
    private MainGUI mainGUI;

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

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var children = container.getChildren();
        this.mainGUI.getStartupLogic().getLanguageManager().getAllLanguages().forEach(language -> {

            var parentGroup = new SettingsGroup();
            parentGroup.setTitle(language.getName());
            parentGroup.setPrefWidth(710);
            parentGroup.getStyleClass().add("gridpane-theme");


            var gridPane = new GridPane();
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.getColumnConstraints().addAll(new ColumnConstraints(165), new ColumnConstraints(165));
            gridPane.getRowConstraints().addAll(new RowConstraints(50), new RowConstraints(30));

            var label = new Label();
            label.setTextAlignment(TextAlignment.LEFT);
            label.setPrefWidth(320);
            label.setMinWidth(320);
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setValignment(label, VPos.CENTER);

            gridPane.add(label, 0, 0, 2, 1);

            var downloadLSP = createButton("Download LSP");
            var downloadRuntime = createButton("Download Runtime");

            gridPane.addRow(1, downloadLSP, downloadRuntime);

            parentGroup.getChildren().add(gridPane);
            children.add(parentGroup);

            var hasLSP = language.hasLSP();
            var hasRuntime = language.hasRuntime();
            if (hasLSP) {
                downloadLSP.setDisable(true);
            } else {
                downloadLSP.setOnAction(event -> {
                    if (language.installLSP()) {
                        LOGGER.info("Install was successful!");
                    } else {
                        LOGGER.info("Install was NOT successful");
                    }
                });
            }

            if (hasRuntime) {
                downloadRuntime.setDisable(true);
            } else {
                Browse.browse(language.downloadRuntimeLink());
            }

            var text = "Language is fully set up";
            var colorClass = "lsp-language-red";
            if (!hasLSP && !hasRuntime) {
                text = "Both the LSP and Runtime of this language have not been set up";
            } else if (!hasLSP) {
                text = "LSP not found for this language";
            } else if (!hasRuntime) {
                text = "Runtime not found for this language";
            } else {
                colorClass = "lsp-language-green";
            }

            label.setText(text);
            label.getStyleClass().add(colorClass);
        });
    }

    private JFXButton createButton(String title) {
        var button = new JFXButton(title);
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setMnemonicParsing(false);
        button.setPrefHeight(30);
        button.setPrefWidth(150);
        button.getStyleClass().add("primary-button");
        return button;
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

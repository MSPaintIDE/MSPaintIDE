package com.uddernetworks.mspaint.gui.fonts;

import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.gui.SettingItem;
import com.uddernetworks.mspaint.main.MainGUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class OCRSettingItem extends Stage implements SettingItem, Initializable {

    @FXML
    private JFXListView<OCRFont> fontSelect;

    private String name;
    private String file;
    private MainGUI mainGUI;

    public OCRSettingItem() {}

    public OCRSettingItem(String name, String file, MainGUI mainGUI) {
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

        this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of("#fontSelect", "dark"));

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var group = new ToggleGroup();
        var list = new ArrayList<FontCell>();
        fontSelect.setCellFactory(t -> {
            var cell = new FontCell(this.mainGUI, group);
            list.add(cell);
            return cell;
        });

        group.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            var cell = (FontCell) newValue.getUserData();
            cell.getItem().setSelected(true);

            if (oldValue != null) {
                var oldCell = (FontCell) oldValue.getUserData();
                oldCell.getItem().setSelected(false);
            }

            System.out.println("=============");
            list.forEach(fontCell -> {
                if (fontCell.getItem() == null) return;
                System.out.println(fontCell.getItem().getName() + " " + fontCell.getItem().isSelected());
            });
        }));

        fontSelect.setFocusTraversable(false);

        fontSelect.setSelectionModel(new EmptySelection());

        for (int i = 1; i <= 5; i++) {
            fontSelect.getItems().add(new OCRFont("Name " + i, "path/to/this/shit " + i));
        }
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

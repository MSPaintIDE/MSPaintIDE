package com.uddernetworks.mspaint.main.gui.window;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTreeView;
import com.uddernetworks.mspaint.main.gui.SettingItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SettingsWindow extends Stage implements Initializable {

    @FXML
    private JFXTreeView<SettingItem> tree;

    @FXML
    private AnchorPane content;

    private List<SettingItem> settingItems;

    public SettingsWindow(List<SettingItem> settingItems) throws IOException {
        super();
        this.settingItems = settingItems;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PopupWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("MS Paint IDE");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        show();

        setTitle("MS Paint IDE");
        getIcons().add(icon.getImage());

        setOnShown(event -> {
            System.out.println("shown tree = " + tree);
        });

        setOnShowing(event -> {
            System.out.println("showing tree = " + tree);
        });
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<SettingItem> root;
        tree.setRoot(root = new TreeItem<>(new SettingItem()));
        tree.setShowRoot(false);

        List<TreeItem<SettingItem>> children = root.getChildren();
        children.addAll(settingItems.stream().map(TreeItem::new).collect(Collectors.toList()));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem<SettingItem> selectedItem = newValue;
            System.out.println("Selected Text : " + selectedItem.getValue());
            // do what ever you want

            List<Node> contentChildren = this.content.getChildren();
            contentChildren.clear();

            try {
                contentChildren.add(selectedItem.getValue().getPane());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}

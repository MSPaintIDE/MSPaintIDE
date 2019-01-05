package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXDecorator;
import com.uddernetworks.mspaint.gui.SettingItem;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsWindow extends Stage implements Initializable {

    @FXML
    private TreeView<SettingItem> tree;

    @FXML
    private ScrollPane content;

    private List<SettingItem> settingItems;
    private String startPath;
    private Consumer<Boolean> toggleStuff;

    public SettingsWindow(String startPath) throws IOException {
        this(Arrays.asList(
                new SettingItem("Appearance", "file/Appearance.fxml"),
                new SettingItem("OCR", "file/OCR.fxml"),
                new SettingItem("Image Generation", "file/ImageGeneration.fxml")
        ), startPath);
    }

    public SettingsWindow(List<SettingItem> settingItems, String startPath) throws IOException {
        super();
        this.settingItems = settingItems;
        this.startPath = startPath;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/PopupWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo-small.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Settings");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        show();

        setTitle("Settings");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put("gridpane-theme", "gridpane-theme-dark");
        changeDark.put("theme-text", "dark-text");
        changeDark.put("search-label", "dark");
        changeDark.put("found-context", "dark");
        changeDark.put("language-selection", "language-selection-dark");

        toggleStuff = newValue -> changeDark.forEach((key, value) -> root.lookupAll("." + key)
                .stream()
                .map(Node::getStyleClass)
                .forEach(styles -> {
                    if (newValue) {
                        styles.add(value);
                    } else {
                        while (styles.remove(value));
                    }
                }));

        SettingsManager.onChangeSetting(Setting.DARK_THEME, toggleStuff, boolean.class, true);

        List<TreeItem<SettingItem>> children = tree.getRoot().getChildren();

        if (startPath != null) {
            children.stream().flatMap(x -> Stream.of(x.isLeaf() ? x : x.getChildren())).map(TreeItem.class::cast).forEach(genericItem -> {
                SettingItem item = ((TreeItem<SettingItem>) genericItem).getValue();

                MultipleSelectionModel<TreeItem<SettingItem>> selectionModel = tree.getSelectionModel();
                if (item.toString().equalsIgnoreCase(startPath)) selectionModel.select(genericItem);
            });
        }
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
//            List<Node> contentChildren = this.content.getChildren();
//            contentChildren.clear();

            try {
                this.content.setContent(newValue.getValue().getPane());
                toggleStuff.accept(SettingsManager.getSetting(Setting.DARK_THEME, Boolean.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}

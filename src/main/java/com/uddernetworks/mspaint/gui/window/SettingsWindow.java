package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXDecorator;
import com.uddernetworks.mspaint.gui.AppearanceSettingItem;
import com.uddernetworks.mspaint.gui.LSPSettingItem;
import com.uddernetworks.mspaint.gui.OCRSettingItem;
import com.uddernetworks.mspaint.gui.SettingItem;
import com.uddernetworks.mspaint.gui.kvselection.DefaultSettingItem;
import com.uddernetworks.mspaint.main.MainGUI;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsWindow extends Stage implements Initializable {

    @FXML
    private TreeView<SettingItem> tree;

    @FXML
    private ScrollPane content;

    private MainGUI mainGUI;
    private List<SettingItem> settingItems;
    private String startPath;
    private Consumer<Boolean> toggleStuff;

    public SettingsWindow(MainGUI mainGUI, String startPath) throws IOException {
        this(mainGUI, Arrays.asList(
                new AppearanceSettingItem("Appearance", "file/Appearance.fxml", mainGUI),
                new OCRSettingItem("OCR", "file/OCR.fxml", mainGUI),
                new DefaultSettingItem("Image Generation", "file/ImageGeneration.fxml"),
                new DefaultSettingItem("Injection", "file/Injection.fxml"),
                new LSPSettingItem("Languages", "file/Languages.fxml", mainGUI)
        ), startPath);
    }

    public SettingsWindow(MainGUI mainGUI, List<SettingItem> settingItems, String startPath) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.settingItems = settingItems;
        this.startPath = startPath;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/PopupWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Settings");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Settings");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        toggleStuff = newValue -> Map.of(
                "gridpane-theme", "gridpane-theme-dark",
                "theme-text", "dark-text",
                "search-label", "dark",
                "found-context", "dark",
                "language-selection", "language-selection-dark"
        ).forEach((key, value) -> root.lookupAll("." + key)
                .stream()
                .map(Node::getStyleClass)
                .forEach(styles -> {
                    if (newValue) {
                        styles.add(value);
                    } else {
                        while (styles.remove(value));
                    }
                }));

        SettingsManager.getInstance().onChangeSetting(Setting.DARK_THEME, toggleStuff, true);

        List<TreeItem<SettingItem>> children = tree.getRoot().getChildren();

        if (startPath != null) {
            children.stream().flatMap(x -> Stream.of(x.isLeaf() ? x : x.getChildren())).map(TreeItem.class::cast).forEach(genericItem -> {
                SettingItem item = ((TreeItem<SettingItem>) genericItem).getValue();

                MultipleSelectionModel<TreeItem<SettingItem>> selectionModel = tree.getSelectionModel();
                if (item.toString().equalsIgnoreCase(startPath)) selectionModel.select(genericItem);
            });
        }

        tree.getSelectionModel().select(0);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<SettingItem> root;
        tree.setRoot(root = new TreeItem<>(new DefaultSettingItem()));
        tree.setShowRoot(false);

        List<TreeItem<SettingItem>> children = root.getChildren();
        children.addAll(settingItems.stream().map(TreeItem::new).collect(Collectors.toList()));

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                this.content.setContent(newValue.getValue().getPane());
                this.toggleStuff.accept(SettingsManager.getInstance().getSetting(Setting.DARK_THEME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

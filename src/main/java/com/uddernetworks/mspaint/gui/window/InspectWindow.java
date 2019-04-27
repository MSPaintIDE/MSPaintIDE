package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageManager;
import com.uddernetworks.mspaint.main.CacheUtils;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.newocr.recognition.ScannedImage;
import com.uddernetworks.newocr.utils.ConversionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class InspectWindow extends Stage implements Initializable {

    @FXML
    private JFXTextField imageName;

    @FXML
    private JFXTextField imageDimensions;

    @FXML
    private JFXTextField fileSize;

    @FXML
    private JFXTextField fontSize;

    @FXML
    private JFXTextField imageLanguage;

    @FXML
    private JFXTextField lines;

    @FXML
    private JFXTextField lastCached;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton okay;

    private MainGUI mainGUI;
    private File inspecting;

    public InspectWindow(MainGUI mainGUI, File inspecting) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.inspecting = inspecting;

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/InspectWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Inspecting " + this.inspecting.getName());
        jfxDecorator.setOnCloseButtonAction(() -> Platform.runLater(this::close));

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Inspecting " + this.inspecting.getName());
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        Map<String, String> changeDark = new HashMap<>();
        changeDark.put("gridpane-theme", "gridpane-theme-dark");
        changeDark.put("theme-text", "dark-text");

        SettingsManager.onChangeSetting(Setting.DARK_THEME, newValue ->
                changeDark.forEach((key, value) -> root.lookupAll("." + key)
                        .stream()
                        .map(Node::getStyleClass)
                        .forEach(styles -> {
                            if (newValue) {
                                styles.add(value);
                            } else {
                                styles.remove(value);
                            }
                        })), boolean.class, true);
    }


    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var ocrManager = this.mainGUI.getMain().getOCRManager();
        String name = this.inspecting.getName();
        ScannedImage scannedImage = ocrManager.getScan().scanImage(this.inspecting).stripLeadingSpaces();
        int pxSize = (int) ocrManager.getActions().getFontSize(scannedImage.letterAt(0).get()).getAsDouble();
        int ptSize = ConversionUtils.pixelToPoint(pxSize);

        String nameNoExtension = name.substring(0, name.length() - 4);
        int periodIndex = nameNoExtension.lastIndexOf('.');
        nameNoExtension = periodIndex == -1 ? "" : nameNoExtension.substring(periodIndex + 1);

        LanguageManager languageManager = this.mainGUI.getMain().getLanguageManager();
        String language = languageManager.getLanguageFromFileExtension(nameNoExtension)
                .map(Language::getName)
                .orElse("Unknown");

        BufferedImage image = scannedImage.getOriginalImage();

        long sizeKB = this.inspecting.length() / 1024;
        imageName.setText(name);
        imageDimensions.setText(image.getWidth() + " x " + image.getHeight());
        fileSize.setText(sizeKB + " KB");
        fontSize.setText(ptSize + "pt / " + pxSize + "px");
        imageLanguage.setText(language);
        lines.setText(String.valueOf(scannedImage.getLineCount()));
        lastCached.setText(CacheUtils.getLastCachedFormatted(this.inspecting));

        cancel.setOnAction(event -> Platform.runLater(this::close));
        okay.setOnAction(event -> Platform.runLater(this::close));
    }
}

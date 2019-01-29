package com.uddernetworks.mspaint.gui.window;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class UserInputWindow extends Stage implements Initializable {

    @FXML
    private JFXButton okay;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXTextField userInput;

    @FXML
    private Label promptText;

    private MainGUI mainGUI;
    private String prompt;
    private String initial;
    private boolean placeholder;
    private boolean number;
    private Consumer<String> onOkay;

    public UserInputWindow(MainGUI mainGUI, String prompt, String initial, boolean placeholder, Consumer<String> onOkay) throws IOException {
        this(mainGUI, prompt, initial, placeholder, false, onOkay);
    }

    public UserInputWindow(MainGUI mainGUI, String prompt, String initial, boolean placeholder, boolean number, Consumer<String> onOkay) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.prompt = prompt;
        this.initial = initial;
        this.placeholder = placeholder;
        this.number = number;
        this.onOkay = onOkay;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/UserInput.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("User Input");
        jfxDecorator.setOnCloseButtonAction(() -> {
            onOkay.accept(null);
            Platform.runLater(this::close);
        });

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("Welcome to MS Paint IDE");
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
        this.promptText.setText(this.prompt);

        if (this.placeholder) {
            this.userInput.setPromptText(this.initial);
        } else {
            this.userInput.setText(this.initial);
        }

        if (number) {
            this.userInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    this.userInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }

        cancel.setOnAction(event -> {
            onOkay.accept(null);
            Platform.runLater(this::close);
        });

        okay.setOnAction(event -> {
            onOkay.accept(userInput.getText());
            Platform.runLater(this::close);
        });
    }
}

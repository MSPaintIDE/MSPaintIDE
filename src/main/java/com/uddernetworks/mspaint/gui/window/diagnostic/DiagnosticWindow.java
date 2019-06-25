package com.uddernetworks.mspaint.gui.window.diagnostic;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.gui.kvselection.EmptySelection;
import com.uddernetworks.mspaint.main.MainGUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.eclipse.lsp4j.Diagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DiagnosticWindow extends Stage implements Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(DiagnosticWindow.class);

    @FXML
    private JFXListView<Map.Entry<String, Diagnostic>> diagnosticList;

    @FXML
    private JFXButton cancel;

    private MainGUI mainGUI;
    private DiagnosticManager diagnosticManager;

    public DiagnosticWindow(MainGUI mainGUI, DiagnosticManager diagnosticManager) throws IOException {
        super();
        this.mainGUI = mainGUI;
        this.diagnosticManager = diagnosticManager;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/DiagnosticDisplay.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("IDE Diagnostics");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);
        show();

        setTitle("IDE Diagnostics");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of(
                "#diagnosticWindow", "dark"
        ));
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.diagnosticList.setFocusTraversable(false);
        this.diagnosticList.setSelectionModel(new EmptySelection<>());

        this.diagnosticList.setCellFactory(t -> new DiagnosticCell(this.mainGUI));

        this.diagnosticManager.onDiagnosticChange(diagnostics -> {
            Platform.runLater(() -> {
                var items = this.diagnosticList.getItems();
                items.removeIf(Predicate.not(diagnostics::contains));
                diagnostics.stream().filter(Predicate.not(items::contains)).forEach(items::add);
            });
        });

        cancel.setOnAction(event -> close());
    }
}

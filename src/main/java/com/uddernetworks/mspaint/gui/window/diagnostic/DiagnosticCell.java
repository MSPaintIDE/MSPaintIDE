package com.uddernetworks.mspaint.gui.window.diagnostic;

import com.uddernetworks.mspaint.main.MainGUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DiagnosticCell extends ListCell<Diagnostic> {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Canvas icon;

    @FXML
    private Label time;

    @FXML
    private Label message;

    private FXMLLoader fxmlLoader;
    private MainGUI mainGUI;

    public DiagnosticCell(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    @Override
    public void updateItem(Diagnostic diagnostic, boolean empty) {
        super.updateItem(diagnostic, empty);

        if (empty || diagnostic == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gui/DiagnosticCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO: Change
                this.mainGUI.getThemeManager().onDarkThemeChange(anchor, Map.of(
                        ".search-label", "dark",
                        ".found-context", "dark"
                ));
            }

            appendIcon(this.icon.getGraphicsContext2D(), diagnostic.getSeverity());

            this.time.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            this.message.setText(diagnostic.getMessage());

            // TODO: Add initial check if it's dark theme?

            setText(null);
            setGraphic(anchor);
        }
    }

    private void appendIcon(GraphicsContext gc, DiagnosticSeverity severity) {
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, 25, 25);

        var path = "";
        Color fill = null;
        switch (severity) {
            case Error:
                fill = Color.RED;
                path = "M11 15h2v2h-2zm0-8h2v6h-2zm.99-5C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z";
                break;
            case Warning:
                fill = new Color(100/255D, 71/255D, 0/255D, 1D);
                path = "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                break;
            case Information:
                fill = new Color(0, 55/255D, 100/255D, 1D);
                path = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
                break;
            case Hint:
                fill = new Color(100/255D, 71/255D, 0, 1D);
                path = "M3.55 18.54l1.41 1.41 1.79-1.8-1.41-1.41-1.79 1.8zM11 22.45h2V19.5h-2v2.95zM4 10.5H1v2h3v-2zm11-4.19V1.5H9v4.81C7.21 7.35 6 9.28 6 11.5c0 3.31 2.69 6 6 6s6-2.69 6-6c0-2.22-1.21-4.15-3-5.19zm5 4.19v2h3v-2h-3zm-2.76 7.66l1.79 1.8 1.41-1.41-1.8-1.79-1.4 1.4z";
                break;
        }

        gc.setFill(fill);
        gc.setStroke(Color.TRANSPARENT);
        gc.appendSVGPath(path);

        gc.fill();
        gc.stroke();
    }
}
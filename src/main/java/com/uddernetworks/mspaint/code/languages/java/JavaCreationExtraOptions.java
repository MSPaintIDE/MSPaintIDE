package com.uddernetworks.mspaint.code.languages.java;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.languages.ExtraCreationOptions;
import com.uddernetworks.mspaint.main.LetterFileWriter;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.util.Browse;
import com.uddernetworks.newocr.recognition.ScannedImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import static com.uddernetworks.mspaint.texteditor.TextEditorManager.applyPadding;
import static com.uddernetworks.mspaint.texteditor.TextEditorManager.generateLetterGrid;
import static com.uddernetworks.mspaint.texteditor.TextEditorManager.getBiggestCoordinates;

public class JavaCreationExtraOptions extends ExtraCreationOptions {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCreationExtraOptions.class);

    private MainGUI mainGUI;

    @FXML
    private JFXComboBox<JavaBuildSystem> buildSystemSelection;

    @FXML
    private JFXTextField groupId;

    @FXML
    private JFXTextField artifactId;

    @FXML
    private JFXTextField version;

    @FXML
    private JFXButton finish;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton help;

    public JavaCreationExtraOptions(MainGUI mainGUI) throws IOException {
        super();
        this.mainGUI = mainGUI;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/GradleOptions.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("icons/taskbar/ms-paint-logo-colored.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(this, root, false, true, true);
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("Gradle options");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        setScene(scene);
        this.mainGUI.getThemeManager().addStage(this);

        setTitle("Gradle options");
        getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo-taskbar.png")));

        this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of(".search-label", "dark",
                ".found-context", "dark",
                ".language-selection", "language-selection-dark"
        ));
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (var value : JavaBuildSystem.values()) {
            buildSystemSelection.getItems().add(value);
        }

        buildSystemSelection.valueProperty().addListener((observable, oldValue, newValue) -> {
            var usingDefault = newValue == JavaBuildSystem.DEFAULT;
            groupId.setDisable(usingDefault);
            artifactId.setDisable(usingDefault);
            version.setDisable(usingDefault);
        });

        finish.setOnAction(event -> {
            var settings = language.getLanguageSettings();
            var buildSystem = buildSystemSelection.getValue();
            if (buildSystem != JavaBuildSystem.DEFAULT) {
                var groupIdString = groupId.getText();
                var artifactIdString = artifactId.getText();
                var versionString = version.getText();

                if (groupIdString.isBlank() || artifactIdString.isBlank() || versionString.isBlank()) return;

                var parent = ppfProject.getFile().getParentFile();
                LOGGER.info("Creating wrapper in: {}", parent.getAbsolutePath());

                LOGGER.info("Creating gradle project {}:{}:{}", groupIdString, artifactIdString, versionString);

                Commandline.runLiveCommand(Arrays.asList("gradle", "wrapper", "--gradle-version", "5.6.2", "--distribution-type", "all"), parent, "Gradle");

                // TODO: This will fail if the OCR has not been trained yet. This should be resolved somehow, probably by
                //       training the database on CMS if no font has been trained yet.

                try {
                    ImageIO.write(createImage("plugins {\n" +
                            "    id 'java'\n" +
                            "    id 'application'\n" +
                            "}\n" +
                            "\n" +
                            "group '" + groupIdString + "'\n" +
                            "version '" + versionString + "'\n" +
                            "mainClassName = '" + groupIdString + ".Main'\n" +
                            "\n" +
                            "sourceCompatibility = 11\n" +
                            "\n" +
                            "repositories {\n" +
                            "    mavenCentral()\n" +
                            "}\n" +
                            "\n" +
                            "dependencies {\n" +
                            "    testCompile group: 'junit', name: 'junit', version: '4.12'\n" +
                            "}\n"), "png", new File(parent, "build.gradle.png"));

                    ImageIO.write(createImage("rootProject.name = '" + artifactIdString + "'\n"), "png", new File(parent, "settings.gradle.png"));
                } catch (IOException | ExecutionException | InterruptedException e) {
                    LOGGER.error("There was an error creating the build.gradle and settings.gradle");
                }
            }

            settings.setSetting(JavaLangOptions.BUILDSYSTEM, buildSystemSelection.getValue());

            close();
            onComplete.run();
        });

        cancel.setOnAction(event -> {
            close();
            createProjectWindow.close();
        });

        help.setOnAction(event -> Browse.browse("https://github.com/MSPaintIDE/MSPaintIDE/blob/master/README.md"));
    }

    private BufferedImage createImage(String text) throws IOException, ExecutionException, InterruptedException {
        var padding = 15;
        ScannedImage letterGrid = generateLetterGrid(mainGUI.getStartupLogic(), null, text);
        var coords = getBiggestCoordinates(letterGrid);
        applyPadding(letterGrid, padding, padding);

        BufferedImage bufferedImage = new BufferedImage(coords[0] + padding * 2, coords[1] + padding * 2, BufferedImage.TYPE_INT_ARGB);

        LetterFileWriter letterFileWriter = new LetterFileWriter(letterGrid, bufferedImage, null);
        letterFileWriter.writeToFile();

        return letterFileWriter.getImage();
    }

}

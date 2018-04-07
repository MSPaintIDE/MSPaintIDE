package com.uddernetworks.mspaint.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class Test extends Application implements Initializable {

    @FXML
    private TextField inputName;
    @FXML
    private JFXTextField highlightedImage;
    @FXML
    private JFXTextField cacheFile;
    @FXML
    private JFXTextField classOutput;
    @FXML
    private JFXTextField compiledJarOutput;
    @FXML
    private JFXTextField libraryFile;
    @FXML
    private JFXTextField otherFiles;
    @FXML
    private JFXTextField letterDirectory;
    @FXML
    private JFXTextField compilerOutputValue;
    @FXML
    private JFXTextField programOutputValue;

    @FXML
    private JFXButton changeInputImage;
    @FXML
    private JFXButton changeHighlightImage;
    @FXML
    private JFXButton changeCacheFile;
    @FXML
    private JFXButton changeClassOutput;
    @FXML
    private JFXButton changeCompiledJar;
    @FXML
    private JFXButton changeLibraries;
    @FXML
    private JFXButton changeOtherFiles;
    @FXML
    private JFXButton changeLetterDir;
    @FXML
    private JFXButton compilerOutput;
    @FXML
    private JFXButton programOutput;

    private Main main;
    private Stage primaryStage;

    private FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image files", "png");
    private FileFilter txtFilter = new FileNameExtensionFilter("Text document", "txt");
    private FileFilter jarFilter = new FileNameExtensionFilter("JAR Archive", "jar");

    public Test() throws IOException, URISyntaxException {
        System.out.println("WTFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

        this.main = new Main();
        main.start(this);
        this.main.temp = "SET IN TEMP";
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("1111111111111111");

        System.out.println("222222222222222");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        registerThings();
    }

    public void registerThings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Test.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.setTitle("MS Paint IDE");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo.png")));

        primaryStage.show();

        TextArea node = (TextArea) scene.lookup("#output");
        node.setText("Text here");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            builder.append("Line #" + i).append("\n");
        }

        node.setText(builder.toString());

        System.out.println("inputName = " + inputName);
//
//        changeInputImage.setOnAction(event -> {
//            FileChooser fc = new FileChooser();
//            fc.setSelectedExtensionFilter(imageFilter);
////            fc.direc(JFileChooser.FILES_AND_DIRECTORIES);
////            fc.setInitialDirectory(currentFile.getParentFile());
////            fc.setInitialFileName(currentFile.getName());
//            File file = fc.showOpenDialog(primaryStage);
//            System.out.println("file = " + file);
//
////            if (returnVal == JFileChooser.APPROVE_OPTION) {
////                File selected = fc.getSelectedFile();
////                inputName.setText(selected.getAbsolutePath());
////                main.setInputImage(fc.getSelectedFile());
////            }
//        });
    }

    @FXML
    private void changePathButton(ActionEvent event) {
        System.out.println("event = " + event);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Test.initialize");

        inputName.setText(main.getInputImage());
        highlightedImage.setText(main.getHighlightedFile());
        cacheFile.setText(main.getObjectFile());
        compiledJarOutput.setText(main.getJarFile());
        libraryFile.setText(main.getLibraryFile());
        otherFiles.setText(main.getOtherFiles());
        classOutput.setText(main.getClassOutput());
        letterDirectory.setText(main.getLetterDirectory());
        compilerOutputValue.setText(main.getCompilerOutput());
        programOutputValue.setText(main.getAppOutput());
    }
}

package com.uddernetworks.mspaint.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

    @FXML
    private JFXProgressBar progress;

    @FXML
    private Label statusText;

    @FXML
    private JFXCheckBox syntaxHighlight;
    @FXML
    private JFXCheckBox compile;
    @FXML
    private JFXCheckBox execute;
    @FXML
    private JFXCheckBox useProbe;
    @FXML
    private JFXCheckBox useCaches;
    @FXML
    private JFXCheckBox saveCaches;

    @FXML
    private TextArea output;

    private Main main;
    private Stage primaryStage;

    private FileFilter imageFilter = new FileNameExtensionFilter("Image files", "png");
    private FileFilter txtFilter = new FileNameExtensionFilter("Text document", "txt");
    private FileFilter jarFilter = new FileNameExtensionFilter("JAR Archive", "jar");

    public Test() throws IOException, URISyntaxException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.main = new Main();
        main.start(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        registerThings();
    }

    public void setStatusText(String text) {
        Platform.runLater(() -> statusText.setText(text));
    }

    public void updateLoading(double current, double total) {
        Platform.runLater(() -> progress.setProgress(current / total));
    }

    public void setIndeterminate(boolean indeterminate) {
        Platform.runLater(() -> progress.setProgress(indeterminate ? -1 : 1));
    }

    public void registerThings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Test.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);

        primaryStage.setTitle("MS Paint IDE");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo.png")));

        primaryStage.show();

        TextArea node = (TextArea) scene.lookup("#output");
        node.setText("Text here");

        StringBuilder builder = new StringBuilder();

//        for (int i = 0; i < 100; i++) {
//            builder.append("Line #" + i).append("\n");
//        }

        node.setText(builder.toString());
    }

    @FXML
    private void startScan(ActionEvent event) {
        new Thread(() -> {
            try {
                progress.setProgress(0);
                progress.getStyleClass().remove("progressError");

                long start = System.currentTimeMillis();
                main.indexAll(useProbe.isSelected(), useCaches.isSelected(), saveCaches.isSelected());

                if (syntaxHighlight.isSelected()) {
                    main.highlightAll();
                }

                if (compile.isSelected()) {
                    main.compile(execute.isSelected());
                }

                System.out.println("Finished everything in " + (System.currentTimeMillis() - start) + "ms");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        inputName.textProperty().addListener(event -> main.setInputImage(new File(inputName.getText())));

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Error happened! Thread " + thread + " exception: " + exception.getLocalizedMessage());
            exception.printStackTrace();
            System.out.println(progress.getStyleClass());
            progress.setProgress(1);
            progress.getStyleClass().remove("progressError");
            progress.getStyleClass().add("progressError");
            setStatusText("An error has occurred!");
        });

        TextPrintStream textPrintStream = new TextPrintStream(output, System.out);
        PrintStream textOut = new PrintStream(textPrintStream);
        System.setOut(textOut);
        System.setErr(textOut);

        new Thread(() -> {
            try {
                while (true) {
                    textPrintStream.updateText();
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        changeInputImage.setOnAction(event -> {
            File selected = main.getInputImage().isEmpty() ? main.getCurrentJar() : new File(main.getInputImage());
            FileDirectoryChooser.openFileChoser(selected, imageFilter, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                inputName.setText(file.getAbsolutePath());
                main.setInputImage(file);
            });
        });

        highlightedImage.textProperty().addListener(event -> main.setHighlightedFile(new File(inputName.getText())));

        changeHighlightImage.setOnAction(event -> {
            File selected = main.getHighlightedFile().isEmpty() ? main.getCurrentJar() : new File(main.getHighlightedFile());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                highlightedImage.setText(file.getAbsolutePath());
                main.setHighlightedFile(file);
            });
        });

        cacheFile.textProperty().addListener(event -> main.setObjectFile(new File(cacheFile.getText())));

        changeCacheFile.setOnAction(event -> {
            File selected = main.getObjectFile().isEmpty() ? main.getCurrentJar() : new File(main.getObjectFile());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                cacheFile.setText(file.getAbsolutePath());
                main.setObjectFile(file);
            });
        });

        classOutput.textProperty().addListener(event -> main.setClassOutput(new File(classOutput.getText())));

        changeClassOutput.setOnAction(event -> {
            File selected = main.getClassOutput().isEmpty() ? main.getCurrentJar() : new File(main.getClassOutput());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                classOutput.setText(file.getAbsolutePath());
                main.setClassOutput(file);
            });
        });

        compiledJarOutput.textProperty().addListener(event -> main.setJarFile(new File(compiledJarOutput.getText())));

        changeCompiledJar.setOnAction(event -> {
                    File selected = main.getJarFile().isEmpty() ? main.getCurrentJar() : new File(main.getJarFile());
                    FileDirectoryChooser.openFileChoser(selected, jarFilter, JFileChooser.FILES_ONLY, file -> {
                        compiledJarOutput.setText(file.getAbsolutePath());
                        main.setJarFile(file);
                    });
                });

        libraryFile.textProperty().addListener(event -> main.setLibraryFile(new File(libraryFile.getText())));

        changeLibraries.setOnAction(event -> {
            File selected = main.getLibraryFile().isEmpty() ? main.getCurrentJar() : new File(main.getLibraryFile());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                libraryFile.setText(file.getAbsolutePath());
                main.setLibraryFile(file);
            });
        });

        otherFiles.textProperty().addListener(event -> main.setOtherFiles(new File(otherFiles.getText())));

        changeOtherFiles.setOnAction(event -> {
            File selected = main.getOtherFiles().isEmpty() ? main.getCurrentJar() : new File(main.getOtherFiles());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                otherFiles.setText(file.getAbsolutePath());
                main.setOtherFiles(file);
            });
        });

        letterDirectory.textProperty().addListener(event -> main.setLetterDirectory(new File(letterDirectory.getText())));

        changeLetterDir.setOnAction(event -> {
            File selected = main.getLetterDirectory().isEmpty() ? main.getCurrentJar() : new File(main.getLetterDirectory());
            FileDirectoryChooser.openFileChoser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                letterDirectory.setText(file.getAbsolutePath());
                main.setLetterDirectory(file);
            });
        });

        compilerOutputValue.textProperty().addListener(event -> main.setCompilerOutput(new File(compilerOutputValue.getText())));

        compilerOutput.setOnAction(event -> {
            File selected = main.getCompilerOutput().isEmpty() ? main.getCurrentJar() : new File(main.getCompilerOutput());
            FileDirectoryChooser.openFileChoser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                compilerOutputValue.setText(file.getAbsolutePath());
                main.setCompilerOutput(file);
            });
        });

        programOutputValue.textProperty().addListener(event -> main.setAppOutput(new File(programOutputValue.getText())));

        programOutput.setOnAction(event -> {
            File selected = main.getAppOutput().isEmpty() ? main.getCurrentJar() : new File(main.getAppOutput());
            FileDirectoryChooser.openFileChoser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                programOutputValue.setText(file.getAbsolutePath());
                main.setAppOutput(file);
            });
        });
    }

    public TextArea getOutputTextArea() {
        return output;
    }
}

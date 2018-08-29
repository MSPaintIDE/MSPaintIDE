package com.uddernetworks.mspaint.main;

import com.jfoenix.controls.*;
import com.uddernetworks.mspaint.git.GitController;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainGUI extends Application implements Initializable {

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
    private JFXTextField originURL;
    @FXML
    private JFXTextField commitMessage;

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
    private JFXButton createRepo;
    @FXML
    private JFXButton addRemote;
    @FXML
    private JFXButton addFiles;
    @FXML
    private JFXButton commit;
    @FXML
    private JFXButton push;

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
    private JFXButton invertColors;

    @FXML
    private TextArea output;

    @FXML
    private AnchorPane rootAnchor;

    private Main main;
    private Stage primaryStage;
    private boolean darkTheme = false;
    private GitController gitController;

    private FileFilter imageFilter = new FileNameExtensionFilter("Image files", "png");
    private FileFilter txtFilter = new FileNameExtensionFilter("Text document", "txt");
    private FileFilter jarFilter = new FileNameExtensionFilter("JAR Archive", "jar");

    public MainGUI() throws IOException, URISyntaxException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.main = new Main();
        main.start(this);

        this.gitController = new GitController(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        registerThings(primaryStage);
    }

    public Main getMain() {
        return this.main;
    }

    public void setStatusText(String text) {
        Platform.runLater(() -> statusText.setText(text));
    }

    public void updateLoading(double current, double total) {
        resetError();
        Platform.runLater(() -> progress.setProgress(current / total));
    }

    public void setIndeterminate(boolean indeterminate) {
        resetError();
        Platform.runLater(() -> progress.setProgress(indeterminate ? -1 : 1));
    }

    public void registerThings(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Test.fxml"));

        ImageView icon = new ImageView(getClass().getClassLoader().getResource("ms-paint-logo.png").toString());
        icon.setFitHeight(25);
        icon.setFitWidth(25);

        JFXDecorator jfxDecorator = new JFXDecorator(primaryStage, root, false, true, true);
        jfxDecorator.setOnCloseButtonAction(() -> System.exit(0));
        jfxDecorator.setGraphic(icon);
        jfxDecorator.setTitle("MS Paint IDE");

        Scene scene = new Scene(jfxDecorator);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);

        primaryStage.setTitle("MS Paint IDE");
        primaryStage.getIcons().add(icon.getImage());

        primaryStage.show();
    }

    @FXML
    private void startScan(ActionEvent event) {
        new Thread(() -> {
            try {
                if (ToolProvider.getSystemJavaCompiler() == null) {
                    setHaveError();
                    System.out.println("Error: No Java compiler found!" +
                            "You must make sure you ran the IDE with with JDK and not the JRE." +
                            "Please see usage instructions: https://github.com/RubbaBoy/MSPaintIDE/#usage-tutorial");
                    return;
                }

                progress.setProgress(0);
                progress.getStyleClass().remove("progressError");

                long start = System.currentTimeMillis();
                if (main.indexAll(useProbe.isSelected(), useCaches.isSelected(), saveCaches.isSelected()) == -1) return;

                if (syntaxHighlight.isSelected()) {
                    main.highlightAll();
                }

                if (compile.isSelected()) {
                    main.compile(execute.isSelected());
                }

                setStatusText("");
                updateLoading(0, 1);

                System.out.println("Finished everything in " + (System.currentTimeMillis() - start) + "ms");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean shouldUseProbe() {
        return this.useProbe.isSelected();
    }

    public boolean shouldUseCaches() {
        return this.useCaches.isSelected();
    }

    public boolean shouldSaveCaches() {
        return this.saveCaches.isSelected();
    }

    public void setHaveError() {
        Platform.runLater(() -> {
            progress.setProgress(1);
            progress.getStyleClass().remove("progressError");
            progress.getStyleClass().add("progressError");
            setStatusText("An error has occurred!");
        });
    }

    public void resetError() {
        Platform.runLater(() -> {
            progress.setProgress(0);
            progress.getStyleClass().remove("progressError");
        });
    }

    private void setGitFeaturesDisabled(boolean disabled) {
        createRepo.setDisable(disabled);
        addRemote.setDisable(disabled);
        addFiles.setDisable(disabled);
        commit.setDisable(disabled);
        push.setDisable(disabled);
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
        setGitFeaturesDisabled(true);

        inputName.textProperty().addListener(event -> main.setInputImage(new File(inputName.getText())));

        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Error happened! Thread " + thread + " exception: " + exception.getLocalizedMessage());
            exception.printStackTrace();
            System.out.println(progress.getStyleClass());
            setHaveError();
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

        invertColors.setOnAction(event -> {
            this.darkTheme = !this.darkTheme;
            Parent parent = invertColors.getParent().getParent().getParent().getParent();
            parent.lookupAll(".theme-text").forEach(node -> {
                if (this.darkTheme) {
                    node.getStyleClass().add("dark-text");
                    parent.lookup(".gridpane-theme").getStyleClass().add("gridpane-theme-dark");
                    parent.lookup(".output-theme").getStyleClass().add("output-theme-dark");
                    parent.lookup(".invert-colors").getStyleClass().add("invert-colors-white");
                } else {
                    node.getStyleClass().remove("dark-text");
                    parent.lookup(".gridpane-theme").getStyleClass().remove("gridpane-theme-dark");
                    parent.lookup(".output-theme").getStyleClass().remove("output-theme-dark");
                    parent.lookup(".invert-colors").getStyleClass().remove("invert-colors-white");
                }
            });
        });

        this.gitController.getVersion(gitVersion -> {
            if (gitVersion == null) {
                System.out.println("Git not found! Git features will not be available.");
            } else {
                System.out.println("Git found! Version: " + gitVersion);
                setGitFeaturesDisabled(false);
            }
        });

        createRepo.setOnAction(event -> {
//            File selected = main.getInputImage().isEmpty() ? main.getCurrentJar() : new File(main.getInputImage());
//            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> this.gitController.gitInit(file));
            this.gitController.gitInit(new File(main.getInputImage()));
        });

        addFiles.setOnAction(event -> FileDirectoryChooser.openMultiFileChoser(new File(main.getInputImage()), null, JFileChooser.FILES_AND_DIRECTORIES, files -> {
            try {
                this.gitController.addFiles(files);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        addRemote.setOnAction(event -> {
            this.gitController.setRemoteOrigin(originURL.getText());
        });

        commit.setOnAction(event -> {
            this.gitController.commit(commitMessage.getText());
        });

        push.setOnAction(event -> {
            this.gitController.push();
        });

        inputName.textProperty().addListener(event -> main.setInputImage(inputName.getText().trim().equals("") ? null : new File(inputName.getText())));

        changeInputImage.setOnAction(event -> {
            File selected = main.getInputImage().isEmpty() ? main.getCurrentJar() : new File(main.getInputImage());
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                inputName.setText(file.getAbsolutePath());
                main.setInputImage(file);
            });
        });

        highlightedImage.textProperty().addListener(event -> main.setHighlightedFile(highlightedImage.getText().trim().equals("") ? null : new File(highlightedImage.getText())));

        changeHighlightImage.setOnAction(event -> {
            File selected = main.getHighlightedFile().isEmpty() ? main.getCurrentJar() : new File(main.getHighlightedFile());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                highlightedImage.setText(file.getAbsolutePath());
                main.setHighlightedFile(file);
            });
        });

        cacheFile.textProperty().addListener(event -> main.setObjectFile(cacheFile.getText().trim().equals("") ? null : new File(cacheFile.getText())));

        changeCacheFile.setOnAction(event -> {
            File selected = main.getObjectFile().isEmpty() ? main.getCurrentJar() : new File(main.getObjectFile());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                cacheFile.setText(file.getAbsolutePath());
                main.setObjectFile(file);
            });
        });

        classOutput.textProperty().addListener(event -> main.setClassOutput(classOutput.getText().trim().equals("") ? null : new File(classOutput.getText())));

        changeClassOutput.setOnAction(event -> {
            File selected = main.getClassOutput().isEmpty() ? main.getCurrentJar() : new File(main.getClassOutput());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                classOutput.setText(file.getAbsolutePath());
                main.setClassOutput(file);
            });
        });

        compiledJarOutput.textProperty().addListener(event -> main.setJarFile(compiledJarOutput.getText().trim().equals("") ? null : new File(compiledJarOutput.getText())));

        changeCompiledJar.setOnAction(event -> {
            File selected = main.getJarFile().isEmpty() ? main.getCurrentJar() : new File(main.getJarFile());
            FileDirectoryChooser.openFileChooser(selected, jarFilter, JFileChooser.FILES_ONLY, file -> {
                compiledJarOutput.setText(file.getAbsolutePath());
                main.setJarFile(file);
            });
        });

        libraryFile.textProperty().addListener(event -> main.setLibraryFile(libraryFile.getText().trim().equals("") ? null : new File(libraryFile.getText())));

        changeLibraries.setOnAction(event -> {
            File selected = main.getLibraryFile().isEmpty() ? main.getCurrentJar() : new File(main.getLibraryFile());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                libraryFile.setText(file.getAbsolutePath());
                main.setLibraryFile(file);
            });
        });

        otherFiles.textProperty().addListener(event -> main.setOtherFiles(otherFiles.getText().trim().equals("") ? null : new File(otherFiles.getText())));

        changeOtherFiles.setOnAction(event -> {
            File selected = main.getOtherFiles().isEmpty() ? main.getCurrentJar() : new File(main.getOtherFiles());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.FILES_AND_DIRECTORIES, file -> {
                otherFiles.setText(file.getAbsolutePath());
                main.setOtherFiles(file);
            });
        });

        letterDirectory.textProperty().addListener(event -> main.setLetterDirectory(letterDirectory.getText().trim().equals("") ? null : new File(letterDirectory.getText())));

        changeLetterDir.setOnAction(event -> {
            File selected = main.getLetterDirectory().isEmpty() ? main.getCurrentJar() : new File(main.getLetterDirectory());
            FileDirectoryChooser.openFileChooser(selected, null, JFileChooser.DIRECTORIES_ONLY, file -> {
                letterDirectory.setText(file.getAbsolutePath());
                main.setLetterDirectory(file);
            });
        });

        compilerOutputValue.textProperty().addListener(event -> main.setCompilerOutput(compilerOutputValue.getText().trim().equals("") ? null : new File(compilerOutputValue.getText())));

        compilerOutput.setOnAction(event -> {
            File selected = main.getCompilerOutput().isEmpty() ? main.getCurrentJar() : new File(main.getCompilerOutput());
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                compilerOutputValue.setText(file.getAbsolutePath());
                main.setCompilerOutput(file);
            });
        });

        programOutputValue.textProperty().addListener(event -> main.setAppOutput(programOutputValue.getText().trim().equals("") ? null : new File(programOutputValue.getText())));

        programOutput.setOnAction(event -> {
            File selected = main.getAppOutput().isEmpty() ? main.getCurrentJar() : new File(main.getAppOutput());
            FileDirectoryChooser.openFileChooser(selected, imageFilter, JFileChooser.FILES_ONLY, file -> {
                programOutputValue.setText(file.getAbsolutePath());
                main.setAppOutput(file);
            });
        });
    }

    public TextArea getOutputTextArea() {
        return output;
    }
}

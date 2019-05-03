package com.uddernetworks.mspaint.gui.fonts;

import com.jfoenix.controls.JFXListView;
import com.uddernetworks.mspaint.gui.SettingItem;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ThemeManager;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class OCRSettingItem extends Stage implements SettingItem, Initializable {

    @FXML
    private JFXListView<OCRFont> fontSelect;

    @FXML
    private Hyperlink addFontText;

    private String name;
    private String file;
    private MainGUI mainGUI;
    private ThemeManager.ThemeChanger themeChanger;
    private Method addURL;
    private List<String> added = new ArrayList<>();

    public OCRSettingItem(String name, String file, MainGUI mainGUI) {
        this.name = name;
        this.file = file;
        this.mainGUI = mainGUI;
    }

    @Override
    public Pane getPane() throws IOException {
        addPath(MainGUI.APP_DATA.getAbsolutePath());

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(this.file));
        loader.setController(this);
        Parent root = loader.load();
        Node node = root.lookup("*");

        if (!(node instanceof Pane)) throw new LoadException("Root element of " + this.file + " not a pane!");

        this.themeChanger = this.mainGUI.getThemeManager().onDarkThemeChange(root, Map.of(
                "#fontSelect", "dark",
                ".remove-entry", "remove-entry-white"
        ));

        return (Pane) node;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var list = new ArrayList<FontCell>();

        fontSelect.setCellFactory(t -> {
            var cell = new FontCell(this.mainGUI, this.themeChanger, currCell -> {
                currCell.getName().textProperty().addListener(((observable, oldValue, newValue) -> {
                    getAndSaveProject(project -> {
                        project.modifyFontName(currCell.getItem().getName(), newValue);
                    });
                }));

                currCell.getPath().textProperty().addListener(((observable, oldValue, newValue) -> {
                    getAndSaveProject(project -> {
                        project.modifyFontPath(currCell.getItem().getName(), newValue);
                    });
                }));

                currCell.getRemoveEntry().setOnAction(event -> {
                    getAndSaveProject(project -> {
                        var currItem = currCell.getItem();
                        if (currItem == null) return;
                        if (currItem.isSelected()) {
                            if (fontSelect.getItems().size() == 1) return;
                            project.getFonts()
                                    .keySet()
                                    .stream()
                                    .limit(1)
                                    .findFirst()
                                    .ifPresent(newActive -> {
                                        project.setActiveFont(newActive);
                                        fontSelect.getItems()
                                                .stream()
                                                .filter(font -> font.getName().equals(newActive))
                                                .findFirst()
                                                .ifPresent(font -> font.setSelected(true));
                                    });
                        }

                        project.removeFont(currItem.getName());
                        fontSelect.getItems().remove(currItem);
                        list.remove(currCell);

                        updateRadio(list);
                    });
                });

                this.themeChanger.update(100, TimeUnit.MILLISECONDS);
            });
            list.add(cell);
            return cell;
        });

        this.addFontText.setOnAction(event -> {
            getAndSaveProject(project -> {
                var name = getDefaultName(project);
                project.addFont(name, "path/");
                var first = fontSelect.getItems().isEmpty();
                if (first) project.setActiveFont(name);
                fontSelect.getItems().add(new OCRFont(name, "path/", first));
                updateRadio(list);
            });
        });

        fontSelect.setFocusTraversable(false);

        fontSelect.setSelectionModel(new EmptySelection());

        var currProject = ProjectManager.getPPFProject();
        currProject.getFonts().forEach((name, path) -> fontSelect.getItems().add(new OCRFont(name, path, currProject.getActiveFont().equals(name))));
    }

    private void updateRadio(List<FontCell> list) {
        list.stream().filter(cell -> cell.getItem() != null)
                .forEach(cell -> cell.getRadio().setSelected(cell.getItem().isSelected()));
    }

    private String getDefaultName(PPFProject project) {
        var fonts = project.getFonts();
        var base = "Name";
        if (!fonts.containsKey(base)) return base;
        base += " ";
        var i = 0;
        while (true) {
            var temp = base + ++i;
            if (!fonts.containsKey(temp)) return temp;
        }
    }

    private void getAndSaveProject(Consumer<PPFProject> projectConsumer) {
        var currProject = ProjectManager.getPPFProject();
        projectConsumer.accept(currProject);
        ProjectManager.save();
    }

    public void addPath(String path) {
        try {
            if (this.added.contains(path)) return;
            if (this.addURL == null) {
                this.addURL = ClassLoader.getSystemClassLoader().getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                this.addURL.setAccessible(true);
            }

            this.addURL.invoke(ClassLoader.getSystemClassLoader(), path);
            this.added.add(path);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return name;
    }
}

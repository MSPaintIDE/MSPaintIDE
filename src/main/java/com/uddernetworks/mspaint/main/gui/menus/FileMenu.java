package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import com.uddernetworks.mspaint.main.gui.SettingItem;
import com.uddernetworks.mspaint.main.gui.window.CreateProjectWindow;
import com.uddernetworks.mspaint.main.gui.window.SettingsWindow;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileMenu extends MenuBind {

    public FileMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "new.project")
    private void onClickNewProject() {
        try {
            new CreateProjectWindow(this.mainGUI, () -> {
                try {
                    this.mainGUI.registerThings();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BindItem(label = "new.image-file")
    private void onClickNewImageFile() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, JFileChooser.FILES_ONLY, file -> {
            this.mainGUI.createAndOpenImageFile(file);
        });
    }

    @BindItem(label = "new.text-file")
    private void onClickNewTextFile() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, JFileChooser.FILES_ONLY, file -> {
            this.mainGUI.createAndOpenTextFile(file);
        });
    }

    @BindItem(label = "open.project")
    private void onClickOpenProject() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), new FileNameExtensionFilter("Paint Project File", "ppf"), JFileChooser.FILES_ONLY, file -> {
            ProjectManager.switchProject(ProjectManager.readProject(file));
        });
    }

    @BindItem(label = "open.file")
    private void onClickOpenFile() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), null, JFileChooser.FILES_ONLY, file -> {
            try {
                new TextEditorManager(file, this.mainGUI);
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @BindItem(label = "clear-caches")
    private void onClickClearCaches() {
        System.out.println("Clearing caches...");

        File objects = ProjectManager.getPPFProject().getObjectLocation();

        if (objects == null) {
            System.out.println("No cache directory found!");
            return;
        }

        if (objects.isDirectory()) {
            Arrays.stream(objects.listFiles()).forEach(File::delete);
        } else {
            objects.delete();
        }

        System.out.println("Cleared caches!");
    }

    @BindItem(label = "settings")
    private void onClickSettings() throws IOException {
        List<SettingItem> settingItems = Arrays.asList(
                new SettingItem("Appearance", "file\\Appearance.fxml"),
                new SettingItem("OCR", "file\\OCR.fxml")
        );

        new SettingsWindow(settingItems);
    }

    @BindItem(label = "print")
    private void onClickPrint() {
        // TODO: Print
    }

    @BindItem(label = "close-project")
    private void onClickCloseProject() throws IOException {
        this.mainGUI.showWelcomeScreen();
    }

    @BindItem(label = "exit")
    private void onClickExit() {
        ProjectManager.writeRecent();
        ProjectManager.save();
        System.exit(0);
    }
}

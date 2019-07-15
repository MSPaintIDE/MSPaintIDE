package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.CreateProjectWindow;
import com.uddernetworks.mspaint.gui.window.SettingsWindow;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FileMenu extends MenuBind {

    private static Logger LOGGER = LoggerFactory.getLogger(FileMenu.class);

    public FileMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "new.project")
    private void onClickNewProject() {
        try {
            new CreateProjectWindow(this.mainGUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BindItem(label = "new.image-file")
    private void onClickNewImageFile() {
        FileDirectoryChooser.openFileSaver(chooser -> {
            chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile());
            chooser.setSelectedExtensionFilter(ProjectFileFilter.PNG);
        }, file -> {
            CompletableFuture.runAsync(() -> {
                try {
                    var usingFile = file;
                    if (!usingFile.getName().endsWith(".png"))
                        usingFile = new File(usingFile.getAbsolutePath() + ".png");

                    BufferedImage image = new BufferedImage(600, 500, BufferedImage.TYPE_INT_ARGB);
                    MainGUI.clearImage(image);
                    ImageIO.write(image, "png", usingFile);

                    TextEditorManager.openPaint(file, SettingsManager.getInstance().getSetting(Setting.INJECT_AUTO_NEW));
                } catch (IOException | InterruptedException e) {
                    LOGGER.error("Error creating image and opening MS Paint.");
                }
            });
        });
    }

    @BindItem(label = "new.text-file")
    private void onClickNewTextFile() {
        FileDirectoryChooser.openFileSaver(chooser ->
                chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile()), file -> {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.error("An error occurred while creating a new text file", e);
            }
            TextEditorManager.openAsync(file, mainGUI, Setting.INJECT_AUTO_NEW);
        });
    }

    @BindItem(label = "open.project")
    private void onClickOpenProject() {
        FileDirectoryChooser.openFileSelector(chooser -> {
            chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile());
            chooser.setSelectedExtensionFilter(ProjectFileFilter.PPF);
        }, file -> {
            ProjectManager.switchProject(ProjectManager.readProject(file));
            this.mainGUI.refreshProject();
        });
    }

    @BindItem(label = "open.file")
    private void onClickOpenFile() {
        FileDirectoryChooser.openFileSelector(chooser -> {
            chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile());
            chooser.setSelectedExtensionFilter(ProjectFileFilter.PNG);
        }, file -> {
            if (!file.getName().endsWith(".png")) {
                TextEditorManager.openAsync(file, mainGUI, Setting.INJECT_AUTO_OPEN);
            } else {
                CompletableFuture.runAsync(() -> {
                    try {
                        TextEditorManager.openPaint(file, SettingsManager.getInstance().getSetting(Setting.INJECT_AUTO_OPEN));
                    } catch (IOException | InterruptedException e) {
                        LOGGER.error("Error opening MS Paint", e);
                    }
                });
            }
        });
    }

    @BindItem(label = "settings")
    private void onClickSettings() throws IOException {
        new SettingsWindow(this.mainGUI, null);
    }

    @BindItem(label = "show-diagnostics")
    private void onClickShowDiagnostics() {
        this.mainGUI.getStartupLogic().getDiagnosticManager().openGUI();
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

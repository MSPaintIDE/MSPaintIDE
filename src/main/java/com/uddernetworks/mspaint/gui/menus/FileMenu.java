package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.CreateProjectWindow;
import com.uddernetworks.mspaint.gui.window.SettingsWindow;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ProjectFileFilter;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.texteditor.TextEditorManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;

public class FileMenu extends MenuBind {

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
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), ProjectFileFilter.PNG, JFileChooser.FILES_ONLY, file -> {
            this.mainGUI.createAndOpenImageFile(file);
        });
    }

    @BindItem(label = "new.text-file")
    private void onClickNewTextFile() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile().getParentFile(), null, JFileChooser.FILES_ONLY, file -> {
            this.mainGUI.createAndOpenTextFile(file);
        });
    }

    @BindItem(label = "open.project")
    private void onClickOpenProject() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile(), ProjectFileFilter.PPF, JFileChooser.FILES_ONLY, file -> {
            ProjectManager.switchProject(ProjectManager.readProject(file));
            this.mainGUI.refreshProject();
        });
    }

    @BindItem(label = "open.file")
    private void onClickOpenFile() {
        FileDirectoryChooser.openFileChooser(ProjectManager.getPPFProject().getFile().getParentFile(), ProjectFileFilter.PNG, JFileChooser.FILES_ONLY, file -> {
            try {
                new TextEditorManager(file, this.mainGUI);
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @BindItem(label = "clear-project-caches")
    private void onClickClearProjectCaches() {
        System.out.println("Clearing project caches...");

        clearCaches(ProjectManager.getPPFProject().getObjectLocation());

        System.out.println("Cleared project caches!");
    }

    @BindItem(label = "clear-global-caches")
    private void onClickClearGlobalCaches() {
        System.out.println("Clearing global caches...");

        clearCaches(new File(MainGUI.LOCAL_MSPAINT, "global_cache"));

        System.out.println("Cleared global caches!");
    }

    private void clearCaches(File file) {
        if (file == null) {
            System.out.println("No cache directory found!");
            return;
        }

        if (file.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(File::delete);
        } else {
            file.delete();
        }
    }

    @BindItem(label = "settings")
    private void onClickSettings() throws IOException {
        new SettingsWindow(this.mainGUI, null);
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

package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.git.GitController;
import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.UserInputWindow;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.ProjectManager;

import java.io.File;
import java.io.IOException;

public class GitMenu extends MenuBind {

    private GitController gitController;

    public GitMenu(MainGUI mainGUI) {
        super(mainGUI);
        this.gitController = mainGUI.getGitController();
    }

    @BindItem(label = "create-repo")
    public void onClickCreateRepo() {
        this.gitController.gitInit(ProjectManager.getPPFProject().getFile());
    }

    @BindItem(label = "add-files")
    public void onClickAddFiles() {
        FileDirectoryChooser.openMultiFileSelector(chooser ->
                chooser.setInitialDirectory(ProjectManager.getPPFProject().getFile().getParentFile()), files -> {
            try {
                this.gitController.addFiles(files.toArray(File[]::new));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @BindItem(label = "push")
    public void onClickPush() {
        this.gitController.push();
    }

    @BindItem(label = "add-remote")
    public void onClickAddRemote() throws IOException {
        new UserInputWindow(this.mainGUI, "Type in the remote origin to add", this.mainGUI.getOrigin(), false, url -> {
            if (url != null) this.gitController.setRemoteOrigin(url);
        });
    }

    @BindItem(label = "commit")
    public void onClickCommit() throws IOException {
        new UserInputWindow(this.mainGUI, "Type in the remote origin to add", "Commit message", true, url -> {
            if (url != null) {
                try {
                    this.gitController.commit(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

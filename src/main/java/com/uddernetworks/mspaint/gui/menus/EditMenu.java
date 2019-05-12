package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.FindReplaceWindow;
import com.uddernetworks.mspaint.gui.window.InspectWindow;
import com.uddernetworks.mspaint.main.FileDirectoryChooser;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import javafx.application.Platform;

import java.io.IOException;

public class EditMenu extends MenuBind {

    public EditMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "find")
    public void onClickFind() throws IOException {
        new FindReplaceWindow(this.mainGUI, false);
    }

    @BindItem(label = "replace")
    public void onClickReplace() throws IOException {
        new FindReplaceWindow(this.mainGUI, true);
    }

    @BindItem(label = "inspect")
    public void onClickInspect() {
        PPFProject ppfProject = ProjectManager.getPPFProject();

        FileDirectoryChooser.openFileSelector(chooser ->
                chooser.setInitialDirectory(ppfProject.getInputLocation()), file -> Platform.runLater(() -> {
            try {
                new InspectWindow(this.mainGUI, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}

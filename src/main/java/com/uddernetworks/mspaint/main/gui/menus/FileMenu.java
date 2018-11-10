package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import com.uddernetworks.mspaint.main.gui.SettingItem;
import com.uddernetworks.mspaint.main.gui.window.SettingsWindow;
import com.uddernetworks.mspaint.project.ProjectManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileMenu extends MenuBind {

    public FileMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "new.project")
    private void onClickNewProject() {
        System.out.println("FileMenu.onClickNewProject");
    }

    @BindItem(label = "new.file")
    private void onClickNewFile() {
        System.out.println("FileMenu.onClickNewFile");
    }

    @BindItem(label = "settings")
    private void onClickSettings() throws IOException {
        System.out.println("FileMenu.onClickSettings");

        List<SettingItem> settingItems = Arrays.asList(new SettingItem("Appearance", "file\\Appearance.fxml"));

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

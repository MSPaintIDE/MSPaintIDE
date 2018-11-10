package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import com.uddernetworks.mspaint.main.gui.SettingItem;
import com.uddernetworks.mspaint.main.gui.window.SettingsWindow;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileMenu extends MenuBind {

    public FileMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "new")
    private void onClickNew() {
        System.out.println("FileMenu.onClickNew");
    }

    @BindItem(label = "settings")
    private void onClickSettings() throws IOException {
        System.out.println("FileMenu.onClickSettings");

        List<SettingItem> settingItems = Arrays.asList(new SettingItem("Appearance", "file\\Appearance.fxml"));

        new SettingsWindow(settingItems);
    }

    @BindItem(label = "print")
    private void onClickPrint() {
        System.out.println("FileMenu.onClickPrint");
    }

    @BindItem(label = "exit")
    private void onClickExit() {
        System.out.println("FileMenu.onClickExit");
    }
}

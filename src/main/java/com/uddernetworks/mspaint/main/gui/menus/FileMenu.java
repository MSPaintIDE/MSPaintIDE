package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class FileMenu extends MenuBind {

    public FileMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "new")
    private void onClickNew() {
        System.out.println("FileMenu.onClickNew");
    }

    @BindItem(label = "settings")
    private void onClickSettings() {
        System.out.println("FileMenu.onClickSettings");
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

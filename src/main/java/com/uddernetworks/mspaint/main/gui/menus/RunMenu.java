package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class RunMenu extends MenuBind {

    public RunMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "run")
    public void onClickRun() {
        System.out.println("RunMenu.onClickRun");
    }

    @BindItem(label = "build")
    public void onClickBuild() {
        System.out.println("RunMenu.onClickBuild");
    }
}

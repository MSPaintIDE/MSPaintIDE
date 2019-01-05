package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.main.MainGUI;

public class RunMenu extends MenuBind {

    public RunMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "run")
    public void onClickRun() {
        System.out.println("Running...");
        this.mainGUI.fullCompile(true);
    }

    @BindItem(label = "build")
    public void onClickBuild() {
        System.out.println("Building...");
        if (this.mainGUI.getCurrentLanguage().isInterpreted()) {
            this.mainGUI.setHaveError();
            System.err.println("The selected language does not support building.");
            return;
        }

        this.mainGUI.fullCompile(false);
    }
}

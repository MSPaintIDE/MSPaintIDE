package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class HelpMenu extends MenuBind {

    public HelpMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "help")
    public void onClickHelp() {
        System.out.println("HelpMenu.onClickHelp");
    }

    @BindItem(label = "source")
    public void onClickSource() {
        System.out.println("HelpMenu.onClickSource");
    }

    @BindItem(label = "submit-a-bug")
    public void onClickSubmitABug() {
        System.out.println("HelpMenu.onClickSubmitABug");
    }

    @BindItem(label = "about")
    public void onClickAbout() {
        System.out.println("HelpMenu.onClickAbout");
    }
}

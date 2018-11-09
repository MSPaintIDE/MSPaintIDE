package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class EditMenu extends MenuBind {

    public EditMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "find")
    public void onClickFind() {
        System.out.println("EditMenu.onClickFind");
    }

    @BindItem(label = "replace")
    public void onClickReplace() {
        System.out.println("EditMenu.onClickReplace");
    }
}

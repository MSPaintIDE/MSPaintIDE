package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.FindReplaceWindow;
import com.uddernetworks.mspaint.main.MainGUI;

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
}

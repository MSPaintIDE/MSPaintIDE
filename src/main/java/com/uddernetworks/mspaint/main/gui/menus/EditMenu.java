package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import com.uddernetworks.mspaint.main.gui.window.FindReplaceWindow;

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

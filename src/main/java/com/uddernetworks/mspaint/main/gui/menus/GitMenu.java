package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class GitMenu extends MenuBind {

    public GitMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "create-repo")
    public void onClickCreateRepo() {
        System.out.println("GitMenu.onClickCreateRepo");
    }

    @BindItem(label = "add-files")
    public void onClickAddFiles() {
        System.out.println("GitMenu.onClickAddFiles");
    }

    @BindItem(label = "push")
    public void onClickPush() {
        System.out.println("GitMenu.onClickPush");
    }

    @BindItem(label = "add-remote")
    public void onClickAddRemote() {
        System.out.println("GitMenu.onClickAddRemote");
    }

    @BindItem(label = "commit")
    public void onClickCommit() {
        System.out.println("GitMenu.onClickCommit");
    }
}

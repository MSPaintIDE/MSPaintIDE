package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;

public class OCRMenu extends MenuBind {

    public OCRMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "settings")
    public void onClickSettings() {
        System.out.println("OCRMenu.onClickSettings");
    }

    @BindItem(label = "train")
    public void onClickTrain() {
        System.out.println("OCRMenu.onClickTrain");
    }
}

package com.uddernetworks.mspaint.main.gui.menus;

import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.gui.BindItem;
import com.uddernetworks.mspaint.main.gui.MenuBind;
import com.uddernetworks.mspaint.main.settings.Setting;
import com.uddernetworks.mspaint.main.settings.SettingsManager;
import com.uddernetworks.newocr.OCRHandle;

import java.io.File;
import java.io.IOException;

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
        OCRHandle ocrHandle = new OCRHandle(this.mainGUI.getMain().getDatabaseManager());
        String filePath = SettingsManager.getSetting(Setting.TRAIN_IMAGE, String.class);
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("No training file path found, can't train the OCR.");
            return;
        }

        File file = new File(filePath);

        if (!file.isFile()) {
            System.err.println("Invalid training image found, can't train the OCR.");
            return;
        }

        try {
            ocrHandle.trainImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

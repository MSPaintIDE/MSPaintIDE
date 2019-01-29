package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.gui.window.SettingsWindow;
import com.uddernetworks.mspaint.gui.window.UserInputWindow;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.painthook.PaintInjector;

import java.io.IOException;

public class InjectMenu extends MenuBind {

    public InjectMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "settings")
    public void onClickSettings() throws IOException {
        new SettingsWindow(this.mainGUI, "Injection");
    }

    @BindItem(label = "select-process")
    public void onClickSelectProcess() {
        PaintInjector.INSTANCE.initializeButtons();
    }

    @BindItem(label = "process-id")
    public void onClickProcessId() throws IOException {
        new UserInputWindow(this.mainGUI, "The MS Paint Process ID to add buttons to", "Process ID", true, true, processId -> {
            var id = Integer.parseInt(processId);
            System.out.println("Injecting into process " + id);
//            PaintInjector.INSTANCE.initializeButtons(id);
        });
    }
}

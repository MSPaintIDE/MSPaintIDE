package com.uddernetworks.mspaint.gui.menus;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.gui.BindItem;
import com.uddernetworks.mspaint.gui.MenuBind;
import com.uddernetworks.mspaint.main.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunMenu extends MenuBind {

    private static Logger LOGGER = LoggerFactory.getLogger(RunMenu.class);

    public RunMenu(MainGUI mainGUI) {
        super(mainGUI);
    }

    @BindItem(label = "run")
    public void onClickRun() {
        LOGGER.info("Running...");
        this.mainGUI.fullCompile(BuildSettings.DEFAULT);
    }

    @BindItem(label = "stop")
    public void onClickStop() {
        LOGGER.info("Stopping...");
        this.mainGUI.getStartupLogic().getRunningCodeManager().stopRunning();
    }

    @BindItem(label = "build")
    public void onClickBuild() {
        LOGGER.info("Building...");
        if (this.mainGUI.getCurrentLanguage().isInterpreted()) {
            this.mainGUI.setHaveError();
            LOGGER.error("The selected language does not support building.");
            return;
        }

        this.mainGUI.fullCompile(BuildSettings.DONT_EXECUTE);
    }
}

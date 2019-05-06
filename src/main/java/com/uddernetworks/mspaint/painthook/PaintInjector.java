package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.uddernetworks.mspaint.main.Main;
import org.apache.log4j.Logger;

public interface PaintInjector extends Library, StdCallLibrary {
    Logger LOGGER = Logger.getLogger(PaintInjector.class);
    PaintInjector INSTANCE = InitInjector.init();

    void clickBuild(ClickCallback callback);
    void clickRun(ClickCallback callback);
    void clickStop(ClickCallback callback);

    void clickCommit(ClickCallback callback);
    void clickPush(ClickCallback callback);
    void clickPull(ClickCallback callback);

    void initializeButtons();
    void initializeButtonsByID(int processId);

    class InitInjector {
        static PaintInjector init() {
            try {
                System.setProperty("jna.tmpdir", System.getProperty("java.io.tmpdir"));
                Main.getCurrentJar().ifPresent(file -> System.setProperty("jna.library.path", file.getParentFile().getParentFile().getAbsolutePath() + "\\native"));
                var library = (PaintInjector) Native.loadLibrary("PaintInjector", PaintInjector.class);
                LOGGER.info("Loaded PaintInjector.dll");
                return library;
            } catch (UnsatisfiedLinkError e) {
                LOGGER.error("Error loading PaintInjector.jar or library", e);
                return null;
            }
        }
    }
}

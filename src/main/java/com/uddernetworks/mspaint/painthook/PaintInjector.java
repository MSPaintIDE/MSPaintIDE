package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
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
                var paintInjectorPath = System.getenv("PaintInjector");
                if (paintInjectorPath == null) {
                    LOGGER.error("Couldn't find system environment variable 'PaintInjector' specifying where PaintInjector.dll is.", new Exception());
                    return null;
                }

                System.setProperty("jna.library.path", paintInjectorPath);
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

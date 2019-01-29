package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface PaintInjector extends Library, StdCallLibrary {
    PaintInjector INSTANCE = InitInjector.init();

    void clickBuild(ClickCallback callback);
    void clickRun(ClickCallback callback);
    void clickStop(ClickCallback callback);

    void clickCommit(ClickCallback callback);
    void clickPush(ClickCallback callback);
    void clickPull(ClickCallback callback);

    void initializeButtons();
//    void initializeButtons(int processId);

    class InitInjector {
        static PaintInjector init() {
            var paintInjectorPath = System.getenv("PaintInjector");
            if (paintInjectorPath == null) {
                new Exception("Couldn't find system environment variable 'PaintInjector' specifying where PaintInjector.dll is.").printStackTrace();
                return null;
            }

            System.setProperty("jna.library.path", paintInjectorPath);
            return (PaintInjector) Native.loadLibrary("PaintInjector", PaintInjector.class);
        }
    }
}

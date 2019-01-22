package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface PaintInjector extends Library, StdCallLibrary {
    PaintInjector INSTANCE = Native.loadLibrary("PaintInjector", PaintInjector.class);

    void clickBuild(ClickCallback callback);
    void clickRun(ClickCallback callback);
    void clickStop(ClickCallback callback);

    void clickCommit(ClickCallback callback);
    void clickPush(ClickCallback callback);
    void clickPull(ClickCallback callback);

    void initializeButtons();
}

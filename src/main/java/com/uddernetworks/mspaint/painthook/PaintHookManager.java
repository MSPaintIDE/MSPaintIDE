package com.uddernetworks.mspaint.painthook;

import com.sun.jna.Library;

public class PaintHookManager {

    public static void main(String[] args) throws InterruptedException {
        new PaintHookManager().startHook();
    }

    interface Test extends Library {}

    public void startHook() throws InterruptedException {
//        System.setProperty("jna.library.path", "");


        PaintInjector.INSTANCE.initializeButtons();

        PaintInjector.INSTANCE.clickPull(() -> {
            System.out.println("Clicked Pull!");
        });

        Thread.sleep(100_000);
    }

}

package com.uddernetworks.mspaint.main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class JFXWorkaround {

    // Required due to https://github.com/javafxports/openjdk-jfx/issues/236
    public static void main(String[] args) {
        try {
            MainGUI.main(args);
        } catch (IOException | InterruptedException | ReflectiveOperationException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}

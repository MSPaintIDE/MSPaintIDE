package com.uddernetworks.mspaint.main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class JFXWorkaround {

    public static void main(String[] args) {
        try {
            MainGUI.main(args);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}

package com.uddernetworks.mspaint.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public class JFXWorkaround {

    public static void main(String[] args) throws IOException {
//        JOptionPane.showMessageDialog(null, "The JVM is running");

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("Exception on " + t.getName());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            for (var stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement.toString());
            }
            e.printStackTrace(System.out);
            e.printStackTrace();
        });

        var fiule = new File("C:\\Users\\RubbaBoy\\AppData\\Local\\MSPaintIDE\\log.txt");
        fiule.getParentFile().mkdirs();
        fiule.createNewFile();
        PrintStream o = new PrintStream(fiule);
        System.setOut(o);

        try {
            MainGUI.main(args);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}

package com.uddernetworks.mspaint.main;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FileDirectoryChooser {

    private static JFileChooser fileChooser;
    private static JDialog jDialog;

    // This is a horrible way of doing this, before anyone yells at me, the standard JavaFX file chooser
    // doesn't allow file and directory selection in the same window
    public static void openMultiFileChoser(File selectedFile, FileFilter fileFilter, int type, Consumer<File[]> onSelected) {
        if (fileChooser != null) {
            fileChooser.hide();
            jDialog.hide();
        }

        CompletableFuture.runAsync(() -> {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(type);
            fileChooser.setSelectedFile(selectedFile);
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

            CompletableFuture.runAsync(() -> {
                try {
                    Field dialogField = JFileChooser.class.getDeclaredField("dialog");
                    dialogField.setAccessible(true);

                    // Sketchy while loop fixes Issue #1
                    jDialog = (JDialog) dialogField.get(fileChooser);
                    while (jDialog == null) {
                        Thread.sleep(100);
                        jDialog = (JDialog) dialogField.get(fileChooser);
                    }
                } catch (NoSuchFieldException | IllegalAccessException | InterruptedException e) {
                    e.printStackTrace();
                }

                jDialog.toFront();
            });

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                onSelected.accept(fileChooser.getSelectedFiles());
            }
        });
    }

    // This is a horrible way of doing this, before anyone yells at me, the standard JavaFX file chooser
    // doesn't allow file and directory selection in the same window
    public static void openFileChooser(File selectedFile, FileFilter fileFilter, int type, Consumer<File> onSelected) {
        if (fileChooser != null) {
            fileChooser.hide();
            jDialog.hide();
        }

        CompletableFuture.runAsync(() -> {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(type);
            fileChooser.setSelectedFile(selectedFile);
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

            CompletableFuture.runAsync(() -> {
                try {
                    Field dialogField = JFileChooser.class.getDeclaredField("dialog");
                    dialogField.setAccessible(true);

                    // Sketchy while loop fixes Issue #1
                    jDialog = (JDialog) dialogField.get(fileChooser);
                    while (jDialog == null) {
                        Thread.sleep(100);
                        jDialog = (JDialog) dialogField.get(fileChooser);
                    }
                } catch (NoSuchFieldException | IllegalAccessException | InterruptedException e) {
                    e.printStackTrace();
                }

                jDialog.toFront();
            });

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                onSelected.accept(fileChooser.getSelectedFile());
            }
        });
    }

}

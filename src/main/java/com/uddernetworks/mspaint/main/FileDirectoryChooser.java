package com.uddernetworks.mspaint.main;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class FileDirectoryChooser {

    private static JFileChooser fileChooser;
    private static JDialog jDialog;

    // This is a horrible way of doing this, before anyone yells at me, the standard JacaFX file chooser
    // doesn't allow file and directory selection in the same window
    public static void openFileChoser(File selectedFile, FileFilter fileFilter, int type, Consumer<File> onSelected) {
        if (fileChooser != null) {
            fileChooser.hide();
            jDialog.hide();
        }

        new Thread(() -> {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(type);
            fileChooser.setSelectedFile(selectedFile);
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    Field dialogField = JFileChooser.class.getDeclaredField("dialog");
                    dialogField.setAccessible(true);
                    jDialog = (JDialog) dialogField.get(fileChooser);
                    jDialog.toFront();
                } catch (NoSuchFieldException | IllegalAccessException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                onSelected.accept(fileChooser.getSelectedFile());
            }
        }).start();
    }

}

package com.uddernetworks.mspaint.main;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FileDirectoryChooser {

    private static final AtomicReference<CustomChooser> fileChooser = new AtomicReference<>();

    // This is a horrible way of doing this, before anyone yells at me, the standard JavaFX file chooser
    // doesn't allow file and directory selection in the same window
    public static void openMultiFileChoser(File selectedFile, FileFilter fileFilter, int type, Consumer<File[]> onSelected) {
        if (fileChooser.get() != null) {
            fileChooser.get().hide();
        }

        CompletableFuture.runAsync(() -> {
            fileChooser.set(new CustomChooser(type, selectedFile, fileFilter, true));

            fileChooser.get().onApproveMultiple(onSelected);
        });
    }

    // This is a horrible way of doing this, before anyone yells at me, the standard JavaFX file chooser
    // doesn't allow file and directory selection in the same window
    public static void openFileChooser(File selectedFile, FileFilter fileFilter, int type, Consumer<File> onSelected) {
        System.out.println("FileDirectoryChooser.openFileChooser");
        if (fileChooser.get() != null) {
            fileChooser.get().hide();
        }

        CompletableFuture.runAsync(() -> {
            System.out.println("Async");
            try {
                fileChooser.set(new CustomChooser(type, selectedFile, fileFilter));

                fileChooser.get().onApproveSingle(onSelected);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        });
    }

    static class CustomChooser extends JFileChooser {

        private final int type;
        private final File selectedFile;
        private final FileFilter fileFilter;
        private boolean multiSelection;

        private JDialog dialog;

        public CustomChooser(int type, File selectedFile, FileFilter fileFilter) {
            this(type, selectedFile, fileFilter, false);
        }

        public CustomChooser(int type, File selectedFile, FileFilter fileFilter, boolean multiSelection) {
            this.type = type;
            this.selectedFile = selectedFile;
            this.fileFilter = fileFilter;
            this.multiSelection = multiSelection;
        }

        protected JDialog createDialog(Component parent) throws HeadlessException {
            this.dialog = super.createDialog(parent);
            System.out.println("Creating bullshit");

            setFileSelectionMode(this.type);
            setSelectedFile(this.selectedFile);
            setFileFilter(this.fileFilter);
            setMultiSelectionEnabled(this.multiSelection);
            setDialogType(JFileChooser.OPEN_DIALOG);

            this.dialog.toFront();
            return this.dialog;
        }

        @Override
        public void hide() {
            super.hide();
            if (this.dialog != null) this.dialog.hide();
        }

        public void onApproveSingle(Consumer<File> callback) {
            if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                callback.accept(getSelectedFile());
            }
        }

        public void onApproveMultiple(Consumer<File[]> callback) {
            if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                callback.accept(getSelectedFiles());
            }
        }
    }

}

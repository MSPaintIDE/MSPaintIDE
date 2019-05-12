package com.uddernetworks.mspaint.main;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FileDirectoryChooser {

    public static void openFileSelector(Consumer<FileChooser> chooserModifier, Consumer<File> onSelected) {
        System.out.println("Async 1");
//        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Async 2");
                var fileChooser = new FileChooser();
                chooserModifier.accept(fileChooser);
                System.out.println("Asynbc!");
                var selected = fileChooser.showOpenDialog(null);
                if (selected != null) onSelected.accept(selected);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
//        });
    }

    public static void openMultiFileSelector(Consumer<FileChooser> chooserModifier, Consumer<List<File>> onSelected) {
        System.out.println("FileDirectoryChooser.openMultiFileSelector");
        var fileChooser = new FileChooser();
        chooserModifier.accept(fileChooser);
        CompletableFuture.runAsync(() -> onSelected.accept(fileChooser.showOpenMultipleDialog(null)));
    }

    public static void openFileSaver(Consumer<FileChooser> chooserModifier, Consumer<File> onSave) {
        System.out.println("FileDirectoryChooser.openFileSaver");
        var fileChooser = new FileChooser();
        chooserModifier.accept(fileChooser);
        CompletableFuture.runAsync(() -> onSave.accept(fileChooser.showSaveDialog(null)));
    }

    public static void openDirectorySelector(Consumer<DirectoryChooser> chooserModifier, Consumer<File> onSelected) {
        System.out.println("FileDirectoryChooser.openDirectorySelector");
        var fileChooser = new DirectoryChooser();
        chooserModifier.accept(fileChooser);
        CompletableFuture.runAsync(() -> onSelected.accept(fileChooser.showDialog(null)));
    }

}

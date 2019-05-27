package com.uddernetworks.mspaint.main;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class FileDirectoryChooser {

    public static void openFileSelector(Consumer<FileChooser> chooserModifier, Consumer<File> onSelected) {
         var fileChooser = new FileChooser();
         chooserModifier.accept(fileChooser);
        Optional.ofNullable(fileChooser.showOpenDialog(null)).ifPresent(onSelected);
    }

    public static void openMultiFileSelector(Consumer<FileChooser> chooserModifier, Consumer<List<File>> onSelected) {
        var fileChooser = new FileChooser();
        chooserModifier.accept(fileChooser);
        Optional.ofNullable(fileChooser.showOpenMultipleDialog(null)).ifPresent(onSelected);
    }

    public static void openFileSaver(Consumer<FileChooser> chooserModifier, Consumer<File> onSave) {
        var fileChooser = new FileChooser();
        chooserModifier.accept(fileChooser);
        Optional.ofNullable(fileChooser.showSaveDialog(null)).ifPresent(onSave);
    }

    public static void openDirectorySelector(Consumer<DirectoryChooser> chooserModifier, Consumer<File> onSelected) {
        var fileChooser = new DirectoryChooser();
        chooserModifier.accept(fileChooser);
        Optional.ofNullable(fileChooser.showDialog(null)).ifPresent(onSelected);
    }

    public static File givenOrParentDir(File file) {
        if (file.isDirectory()) return file;
        return file.getParentFile();
    }

}

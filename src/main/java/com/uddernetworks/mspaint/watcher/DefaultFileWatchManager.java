package com.uddernetworks.mspaint.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultFileWatchManager implements FileWatchManager {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultFileWatcher.class);

    private List<FileWatcher> fileWatchers = new ArrayList<>();

    @Override
    public boolean anythingWatching(File file) {
        return getWatcher(file).isPresent();
    }

    @Override
    public Optional<FileWatcher> getWatcher(File file) {
        return fileWatchers.stream().filter(watcher -> watcher.hasCovered(file)).findFirst();
    }

    @Override
    public FileWatcher watchFile(File file) {
        var optionalWatcher = fileWatchers.stream().filter(watcher -> watcher.hasCovered(file)).findFirst();
        if (optionalWatcher.isPresent()) return optionalWatcher.get();
        LOGGER.info("Creating a FileWatcher for {}", file.getAbsolutePath());
        LOGGER.error("Test", new Exception("Test trace"));
        var watcher = new DefaultFileWatcher(file);
        this.fileWatchers.add(watcher);
        return watcher;
    }

    @Override
    public void removeWatcher(FileWatcher fileWatcher) {
        fileWatcher.stopWatching();
        fileWatcher.clearListeners();
        this.fileWatchers.remove(fileWatcher);
    }
}

package com.uddernetworks.mspaint.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class DefaultFileWatcher implements FileWatcher {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultFileWatcher.class);

    private static final int MIN_TIME_IN_MILLS = 250;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private File file;
    private boolean watching;
    private Future<?> watchingFuture;
    private Map<Integer, BiConsumer<WatchType, File>> fileListeners = new HashMap<>();
    private int lastUsedID = 0;

    public DefaultFileWatcher(File file) {
        this.file = file;
    }

    @Override
    public void startWatching() {
        this.watching = true;
        this.watchingFuture = executor.submit(() -> {
            try {
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    this.file.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

                    var last = System.currentTimeMillis();
                    while (true) {
                        var watchKey = watchService.take();
                        Map<WatchType, File> found = null;
                        for (var event : watchKey.pollEvents()) {
                            var changed = ((Path) event.context()).toAbsolutePath().toFile();
                            if (hasCovered(changed)) {
                                if (found == null) found = new HashMap<>();
                                found.put(WatchType.fromKind(event.kind()), changed);
                            }
                        }

                        if (found != null && (System.currentTimeMillis() - last) > MIN_TIME_IN_MILLS) {
                            found.forEach((type, file) -> this.fileListeners.values().forEach(listener -> listener.accept(type, file)));
                            last = System.currentTimeMillis();
                        }

                        if (!watchKey.reset()) {
                            LOGGER.info("Key has been unregistered");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("An exception has occurred in a FileWatcher!", e);
            } catch (InterruptedException ignored) {}
        });
    }

    @Override
    public void stopWatching() {
        if (!isWatching()) return;
        this.watchingFuture.cancel(true);
        this.watching = false;
    }

    @Override
    public int addListener(BiConsumer<WatchType, File> onChange) {
        this.fileListeners.put(++this.lastUsedID, onChange);
        return this.lastUsedID;
    }

    @Override
    public void removeListener(int id) {
        this.fileListeners.remove(id);
    }

    @Override
    public void clearListeners() {
        this.fileListeners.clear();
    }

    @Override
    public boolean hasCovered(File file) {
        if (file.equals(this.file)) return true;
        return this.file.toURI().compareTo(file.toURI()) < 0;
    }

    @Override
    public boolean isWatching() {
        return this.watching;
    }
}

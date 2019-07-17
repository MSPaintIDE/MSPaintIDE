package com.uddernetworks.mspaint.watcher;

import com.sun.nio.file.ExtendedWatchEventModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DefaultFileWatcher implements FileWatcher {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultFileWatcher.class);

    private static final int MIN_TIME_IN_MILLS = 250;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private File file;
    private boolean watching;
    private Future<?> watchingFuture;
    private Map<Integer, BiConsumer<WatchType, File>> fileListeners = new HashMap<>();
    private int lastUsedID = 0;
    private List<Function<File, Boolean>> fileFilters = new ArrayList<>();

    public DefaultFileWatcher(File file) {
        this.file = file;
    }

    @Override
    public FileWatcher startWatching() {
        this.watching = true;
        this.watchingFuture = executor.submit(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                this.file.toPath().register(watchService,
                        new WatchEvent.Kind<?>[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE},
                        ExtendedWatchEventModifier.FILE_TREE);

                var cooldownMap = new HashMap<File, Long>();
                var lastActionMap = new HashMap<File, WatchType>();
                while (true) {
                    var watchKey = watchService.take();
                    Map<WatchType, File> found = null;
                    for (var event : watchKey.pollEvents()) {
                        var dir = (Path) watchKey.watchable();
                        var changed = dir.resolve((Path) event.context()).toFile();
                        if (hasCovered(changed)) {
                            if (found == null) found = new HashMap<>();
                            found.put(WatchType.fromKind(event.kind()), changed);
                        }
                    }

                    if (found != null) {
                        found.forEach((type, file) -> {
                            if (!keepFromFilters(file)) return;

                            if (compareTypes(lastActionMap.get(file), type) && System.currentTimeMillis() - cooldownMap.getOrDefault(file, 0L) < MIN_TIME_IN_MILLS) return;

                            lastActionMap.put(file, type);

                            cooldownMap.put(file, System.currentTimeMillis());
                            this.fileListeners.values().forEach(listener -> listener.accept(type, file));
                        });
                    }

                    if (!watchKey.reset()) {
                        LOGGER.info("Key has been unregistered");
                    }
                }
            } catch (IOException e) {
                LOGGER.error("An exception has occurred in a FileWatcher!", e);
            } catch (InterruptedException ignored) {}
        });

        return this;
    }

    private boolean compareTypes(WatchType gotten, WatchType type) {
        if (type == gotten) return true;
        if (type == WatchType.CREATE && gotten == WatchType.MODIFY) return true;
        return type == WatchType.MODIFY && gotten == WatchType.CREATE;
    }

    @Override
    public FileWatcher stopWatching() {
        if (!isWatching()) return this;
        this.watchingFuture.cancel(true);
        this.watching = false;
        return this;
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

    @Override
    public void addFileFiler(Function<File, Boolean> filter) {
        this.fileFilters.add(filter);
    }

    @Override
    public void clearFilters() {
        this.fileFilters.clear();
    }

    @Override
    public boolean keepFromFilters(File file) {
        return this.fileFilters.stream().allMatch(filter -> filter.apply(file));
    }
}

package com.uddernetworks.mspaint.watcher;

import java.io.File;
import java.util.Optional;

public interface FileWatchManager {

    /**
     * Gets if any {@link FileWatcher} registered is watching the given file.
     *
     * @param file The file to check
     * @return If any {@link FileWatcher} is watching the given file
     */
    boolean anythingWatching(File file);

    /**
     * Gets the {@link FileWatcher}, if any, watching the given file.
     *
     * @param file The file to get the {@link FileWatcher} for
     * @return The {@link FileWatcher}, if any
     */
    Optional<FileWatcher> getWatcher(File file);

    /**
     * Creates or returns a {@link FileWatcher} to watch the given file.
     *
     * @param file The file to watch
     * @return The {@link FileWatcher} to watch the given file
     */
    FileWatcher watchFile(File file);

    /**
     * Removes and stops the given {@link FileWatcher}.
     *
     * @param fileWatcher The {@link FileWatcher} to stop and remove
     */
    void removeWatcher(FileWatcher fileWatcher);

}

package com.uddernetworks.mspaint.watcher;

import java.io.File;
import java.util.function.BiConsumer;

public interface FileWatcher {

    /**
     * Starts watching the file given in the constructor.
     *
     * @return The current {@link FileWatcher}
     */
    FileWatcher startWatching();

    /**
     * Stops any watching of the file.
     *
     * @return The current {@link FileWatcher}
     */
    FileWatcher stopWatching();

    /**
     * Adds a listener to be invoked when the given file(s) are changed.
     *
     * @param onChange Invoked when the file is changed, the consumer being given the file changed and the
     *                 {@link WatchType} of the change
     * @return The ID of the listener, to optionally be removed later on
     */
    int addListener(BiConsumer<WatchType, File> onChange);

    /**
     * Removes the given listeners ID.
     *
     * @param id The ID of the listener to remove
     */
    void removeListener(int id);

    /**
     * Clears all listeners.
     */
    void clearListeners();

    /**
     * Gets if the current {@link FileWatcher} is watching the given file.
     *
     * @param file The file to check
     * @return If the given file is being listened to
     */
    boolean hasCovered(File file);

    /**
     * Gets if the current watcher has started watching anything via {@link FileWatcher#startWatching()}.
     *
     * @return If the watcher is watching
     */
    boolean isWatching();

}

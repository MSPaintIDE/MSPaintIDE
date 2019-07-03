package com.uddernetworks.mspaint.watcher;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    /**
     * Adds a filter to any file that is being changed/deleted/created. If the function returns true, that means the
     * file will be given to any listeners. ALL filters must return true for a file to go through listeners.
     *
     * @param filter The filter every file will pass through
     */
    void addFileFiler(Function<File, Boolean> filter);

    /**
     * Clears all file filters, for whatever reason.
     */
    void clearFilters();

    /**
     * If the file should be kept according to filters added via {@link FileWatcher#addFileFiler(Function)}.
     * Returns true if ALL filters also return true.
     *
     * @param file The file to test
     * @return If all filters returned true for the file
     */
    boolean keepFromFilters(File file);

}

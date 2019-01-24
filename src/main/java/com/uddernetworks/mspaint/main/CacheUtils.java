package com.uddernetworks.mspaint.main;

import com.uddernetworks.mspaint.project.ProjectManager;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CacheUtils {

    public static final File GLOBAL_CACHE = new File(MainGUI.APP_DATA, "global_cache");
    private static final PrettyTime prettyTime = new PrettyTime();

    /**
     * Gets the cache .json file for the given image file.
     *
     * @param file The image file to get the cache file
     * @param internal If the file is an internal file, or an arbitrary external file
     * @return The .json cache file of the given file
     */
    public static File getCacheFor(File file, boolean internal) {
        String name = file.getName();
        String nameStripped = name.substring(0, name.length() - 4);
        File parent = internal ? ProjectManager.getPPFProject().getObjectLocation() : GLOBAL_CACHE;
        return new File(parent, nameStripped + "_cache.json");
    }

    /**
     * Gets the time in milliseconds when the given image file was last cached.
     * The system will first see if this is an internal file, if not, it will check external.
     *
     * @param file The image file to check the cache status
     * @return The time in milliseconds the file was cached last. May return -1 if the cache file is not found
     */
    public static long getLastCached(File file) {
        File cacheFile = getCacheFor(file, true);
        if (!cacheFile.isFile()) cacheFile = getCacheFor(file, false);
        return cacheFile.isFile() ? cacheFile.lastModified() : -1;
    }

    /**
     * Gets the time in milliseconds when the given image file was last cached.
     *
     * @param file The image file to check the cache status
     * @param internal If the file is an internal file, or an arbitrary external file
     * @return The time in milliseconds the file was cached last
     */
    public static long getLastCached(File file, boolean internal) {
        return getCacheFor(file, internal).lastModified();
    }

    /**
     * Gets the formatted time and date when the given image file was last cached.
     * The system will first see if this is an internal file, if not, it will check external.
     * The time is formatted as MM/dd/yyy HH:mm:ss Z
     *
     * @param file The image file to check the cache status
     * @return The formatted time since the file was last cached. May return "No cache found" if there was no
     * internal or external cache found for the given file
     */
    public static String getLastCachedFormatted(File file) {
        File cacheFile = getCacheFor(file, true);
        if (!cacheFile.isFile()) cacheFile = getCacheFor(file, false);
        if (!cacheFile.isFile()) return "No cache found";
        return prettyTime.format(Date.from(LocalDateTime.now().minusSeconds((System.currentTimeMillis() - cacheFile.lastModified()) / 1000).atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * Gets the formatted time and date when the given image file was last cached.
     * The time is formatted as MM/dd/yyy HH:mm:ss Z
     *
     * @param file The image file to check the cache status
     * @param internal If the file is an internal file, or an arbitrary external file
     * @return The formatted time since the file was last cached
     */
    public static String getLastCachedFormatted(File file, boolean internal) {
        long time = getCacheFor(file, internal).lastModified();
        return prettyTime.format(Date.from(LocalDateTime.now().minusSeconds((System.currentTimeMillis() - time) / 1000).atZone(ZoneId.systemDefault()).toInstant()));
    }

}

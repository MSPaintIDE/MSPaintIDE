package com.uddernetworks.mspaint.git;

import java.io.File;
import java.util.Map;

public class GitIndex {
    private Map<String, String> added;

    public GitIndex(Map<String, String> added) {
        this.added = added;
    }

    /**
     * Adds a file to git
     * @param image The image of the code saved by MS Paint
     * @param source The scanned code .java file
     */
    public void addFile(File image, File source) {
        this.added.put(image.getAbsolutePath(), source.getAbsolutePath());
    }
}

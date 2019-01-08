package com.uddernetworks.mspaint.main;

import java.io.File;

public class ModifiedDetector {

    private final File image;
    private final File fileObject;

    public ModifiedDetector(File image, File fileObject) {
        this.image = image;
        this.fileObject = fileObject;
    }

    /**
     * Gets if a file is newer than another
     * @return True if the first file is newer than the second one
     */
    public boolean imageChanged() {
        return fileObject == null || !image.exists() || !fileObject.exists() || fileObject.lastModified() < image.lastModified();
    }

}

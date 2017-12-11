package com.uddernetworks.mspaint.main;

import java.io.File;

public class ModifiedDetector {

    private final File image;
    private final File fileObject;

    public ModifiedDetector(File image, File fileObject) {
        this.image = image;
        this.fileObject = fileObject;
    }

    public boolean imageChanged() {
        return !image.exists() || !fileObject.exists() || fileObject.lastModified() < image.lastModified();

    }

}

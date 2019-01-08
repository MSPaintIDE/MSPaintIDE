package com.uddernetworks.mspaint.main;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProjectFileFilter {
    public static final FileFilter PNG = new FileNameExtensionFilter("Image file", "png");
    public static final FileFilter JAR = new FileNameExtensionFilter("JAR Archive", "jar");
    public static final FileFilter PPF = new FileNameExtensionFilter("Paint Project File", "ppf");
}

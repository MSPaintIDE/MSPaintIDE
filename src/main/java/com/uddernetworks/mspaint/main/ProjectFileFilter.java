package com.uddernetworks.mspaint.main;

import javafx.stage.FileChooser;

public class ProjectFileFilter {
    public static final FileChooser.ExtensionFilter PNG = new FileChooser.ExtensionFilter("Image file", "*.png");
    public static final FileChooser.ExtensionFilter JAR = new FileChooser.ExtensionFilter("JAR Archive", "*.jar");
    public static final FileChooser.ExtensionFilter PPF = new FileChooser.ExtensionFilter("Paint Project File", "*.ppf");
}

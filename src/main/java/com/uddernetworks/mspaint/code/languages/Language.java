package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.OverrideExecute;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.util.IDEFileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Language<G> {

    // Public as to be used in subclasses
    public StartupLogic startupLogic;

    public Language(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    /**
     * Gets the {@link Logger}.
     *
     * @return The {@link Logger}
     */
    public abstract Logger getLogger();

    /**
     * Loads settings for the current language.
     */
    public void loadForCurrent() {
        getLanguageSettings().initOptions();
    }

    /**
     * Gets the name of the Language, e.g. "Java", "Python".
     *
     * @return the name of the language
     */
    public abstract String getName();

    /**
     * Gets the file extensions to be used by files that will be parsed by the language. This does not include the
     * period, examples include "java", "py".
     *
     * @return the file extensions used
     */
    public abstract String[] getFileExtensions();

    /**
     * Gets the extension of the compiled/packaged output file that is generated from the source code. An example of an
     * output of this method is "jar" for Java.
     * The method may return null if the language does not support output/packaged files.
     *
     * @return the extension of the output file
     */
    public abstract String getOutputFileExtension();

    /**
     * Gets the source directory that files are coming from.
     *
     * @return The source directory
     */
    public abstract File getInputLocation();

    /**
     * Gets if the language is interpreted (Compared to being compiled).
     *
     * @return if the language is interpreted
     */
    public abstract boolean isInterpreted();

    /**
     * Gets if the language has the correct software/libraries needed to compile/interpret and execute the language on
     * the system.
     *
     * @return if the system meets the requirements to use the language
     */
    public abstract boolean meetsRequirements();

    /**
     * Gets the language's Lexer for custom highlighting
     *
     * @return the language's implementation of DefaultJFlexLexer
     */
    public abstract DefaultJFlexLexer getLanguageHighlighter();

    /**
     * Gets the {@link LanguageSettings<G>} of the current language.
     *
     * @return The {@link LanguageSettings<G>} used by the current language
     */
    public abstract LanguageSettings<G> getLanguageSettings();

    /**
     * Highlights all {@link ImageClass}s given. When implementing, this method must ALSO check in the settings if
     * highlighting should occur.
     *
     * @param imageClasses The {@link ImageClass}s to highlight
     * @throws IOException If an IO Exception occurs
     */
    public abstract void highlightAll(List<ImageClass> imageClasses) throws IOException;

    /**
     * Gets all the {@link ImageClass}s that will be used during execution/compilation of the program with the current
     * language.
     */
    public abstract Optional<List<ImageClass>> indexFiles();

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @throws IOException If an IO Exception occurs
     */
    public abstract Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException;

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageClasses The {@link ImageClass}s to compile/execute, usually derived from {@link Language#indexFiles(Object)}
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @throws IOException If an IO Exception occurs
     */
    public abstract Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException;

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageClasses The {@link ImageClass}s to compile/execute, usually derived from {@link Language#indexFiles(Object)}
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @param executeOverride The policy for executing or not
     * @throws IOException If an IO Exception occurs
     */
    public abstract Map<ImageClass, List<LanguageError>> compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, OverrideExecute executeOverride) throws IOException;

    /**
     * Intended for internal use only.
     *
     * Gets all the {@link ImageClass}s that will be used during execution/compilation of the program with the current
     * language.
     *
     * @param inputDirectorySetting The setting of a File directory containing all source code images
     * @return All the {@link ImageClass}s to be used during compilation/execution
     */
    protected Optional<List<ImageClass>> indexFiles(G inputDirectorySetting) {
        var LOGGER = getLogger();
        var mainGUI = this.startupLogic.getMainGUI();
        if (optionsNotFilled()) {
            LOGGER.error("Please select files for all options");
            mainGUI.setHaveError();
            return Optional.empty();
        }

        LOGGER.info("Scanning all images...");

        mainGUI.setStatusText(null);

        var inputDirectory = getLanguageSettings().<File>getSetting(inputDirectorySetting);
        var imageClasses = new ArrayList<ImageClass>();

        for (File imageFile : IDEFileUtils.getFilesFromDirectory(inputDirectory, getFileExtensions(), "png")) {
            LOGGER.info("Adding non directory: " + imageFile.getAbsolutePath());
            imageClasses.add(new ImageClass(imageFile, mainGUI));
        }

        mainGUI.setStatusText(null);
        return Optional.of(imageClasses);
    }

    /**
     * Highlights all {@link ImageClass}s given.
     *
     * @param highlightDirectorySetting The setting of a File directory to put all highlights into
     * @param imageClasses The {@link ImageClass}s to highlight
     * @throws IOException If an IO Exception occurs
     */
    protected void highlightAll(G highlightDirectorySetting, List<ImageClass> imageClasses) throws IOException {
        var LOGGER = getLogger();
        var mainGUI = startupLogic.getMainGUI();
        if (optionsNotFilled()) {
            LOGGER.error("Please select files for all options");
            mainGUI.setHaveError();
            return;
        }

        var highlightDirectory = getLanguageSettings().<File>getSetting(highlightDirectorySetting);

        if (highlightDirectory != null && !highlightDirectory.isDirectory()) highlightDirectory.mkdirs();

        if (highlightDirectory == null || !highlightDirectory.isDirectory()) {
            LOGGER.error("No highlighted file directory found!");
            mainGUI.setHaveError();
            return;
        }

        LOGGER.info("Scanning all images...");
        mainGUI.setStatusText("Highlighting...");
        mainGUI.setIndeterminate(true);
        long start = System.currentTimeMillis();

        for (ImageClass imageClass : imageClasses) {
            imageClass.highlight(highlightDirectory);
        }

        mainGUI.setIndeterminate(false);
        mainGUI.setStatusText(null);

        LOGGER.info("Finished highlighting all images in " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Gets if all the required options are filled in.
     *
     * @return If all required options are filled in
     */
    public boolean optionsNotFilled() {
        var ppfProject = ProjectManager.getPPFProject();
        return !getLanguageSettings().requiredFilled() || (getOutputFileExtension() != null && ppfProject.getCompilerOutput() == null);
    }
}

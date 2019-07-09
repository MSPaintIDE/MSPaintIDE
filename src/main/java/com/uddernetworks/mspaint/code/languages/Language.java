package com.uddernetworks.mspaint.code.languages;

import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.execution.ThrowableSupplier;
import com.uddernetworks.mspaint.code.lsp.DefaultLanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.code.lsp.doc.Document;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import com.uddernetworks.mspaint.project.PPFProject;
import com.uddernetworks.mspaint.project.ProjectManager;
import com.uddernetworks.mspaint.util.Browse;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Language {

    // Public as to be used in subclasses
    public StartupLogic startupLogic;
    private PPFProject lastInitted = null;

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
        var current = ProjectManager.getPPFProject();
        if (current.equals(this.lastInitted)) return;
        this.lastInitted = current;
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
     * Gets the input {@link Option} for the language.
     *
     * @return The {@link Option} specifying the lang's input
     */
    public abstract Option getInputOption();

    /**
     * Gets the highlight directory {@link Option} for the language.
     *
     * @return The {@link Option} specifying the project's highlight output directory
     */
    public abstract Option getHighlightOption();

    /**
     * Gets the source directory that files are coming from.
     *
     * @return The source directory
     */
    public File getInputLocation() {
        return getLanguageSettings().getSetting(getInputOption());
    }

    /**
     * Gets the image file where executed programs' console output will go.
     *
     * @return The image file
     */
    public abstract File getAppOutput();

    /**
     * Gets the image file where the compiler/interpreter's console output will go.
     *
     * @return The image file
     */
    public abstract File getCompilerOutput();

    /**
     * Gets if the language is interpreted (Compared to being compiled).
     *
     * @return if the language is interpreted
     */
    public abstract boolean isInterpreted();

    /**
     * Gets the instance of {@link DefaultLanguageServerWrapper} being used by the current {@link Language}.
     *
     * @return The instance of the used {@link DefaultLanguageServerWrapper}
     */
    public abstract LanguageServerWrapper getLSPWrapper();

    /**
     * Gets the parent that is requires for all projects of the current language. If not present, the user will be able
     * to set the project to go anywhere.
     *
     * @return The parent directory of all projects of this language, if necessary
     */
    public Optional<File> getStaticParent() {
        return Optional.empty();
    }

    /**
     * Gets if the language has the correct software/libraries needed to compile/interpret and execute the language on
     * the system.
     *
     * @return if the system meets the requirements to use the language
     */
    public abstract boolean hasLSP();

    /**
     * Downloads and installs the LSP for the current language without any user prompt.
     *
     * @return If the install was successful or not
     */
    public abstract boolean installLSP();

    /**
     * Prompts the user for an installation of the lang's LSP, with options for yes, no, or going to teh given website.
     *
     * @param promptText The text to ask the user if they want to install the LSP
     * @param website The website to direct the user to if they click the "Website" option
     * @param install The code to actually install the LSP. No further checking is required if the LSP should be
     *                installed, as {@link Language#hasLSP()} has been checked before the prompt.
     * @return If the install was successful.
     */
    protected boolean lspInstallHelper(String promptText, String website, ThrowableSupplier<Boolean> install) {
        if (hasLSP()) return false;

        try {
            var res = JOptionPane.showOptionDialog(null,
                    promptText,
                    "Download Confirm", 0, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(Language.class.getResourceAsStream("/icons/popup/save.png"))),
                    new String[]{"Yes", "No", "Website"}, "Yes");
            if (res == 0) {
                return install.get();
            } else if (res == 2) {
                Browse.browse(website);
            }
        } catch (Exception e) {
            getLogger().error("There was an error while trying to install the Java LSP server", e);
        }

        return false;
    }

    /**
     * If the system has the runtime or whatever is needed to compile and run code
     *
     * @return If the system can compile and run code in the current language
     */
    public abstract boolean hasRuntime();

    /**
     * Returns a link to download the runtime. This should not be a direct link.
     *
     * @return A link to download the runtime
     */
    public abstract String downloadRuntimeLink();

    /**
     * Gets the name of the TextMate file used by the language.
     *
     * @return The name of the internal .json TextMate file
     */
    public abstract HighlightData getHighlightData();

    /**
     * Gets the {@link LanguageSettings} of the current language.
     *
     * @return The {@link LanguageSettings} used by the current language
     */
    public abstract LanguageSettings getLanguageSettings();

    /**
     * Highlights all {@link ImageClass}s given. When implementing, this method must ALSO check in the settings if
     * highlighting should occur.
     *
     * @param imageClasses The {@link ImageClass}s to highlight
     * @throws IOException If an IO Exception occurs
     */
    public abstract void highlightAll(List<ImageClass> imageClasses) throws IOException;

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @throws IOException If an IO Exception occurs
     */
    public CompilationResult compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        var imageClassesOptional = indexFiles();
        if (imageClassesOptional.isEmpty()) {
            getLogger().error("Error while finding ImageClasses, aborting...");
            return new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        }

        return compileAndExecute(mainGUI, imageClassesOptional.get(), imageOutputStream, compilerStream);
    }

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageClasses The {@link ImageClass}s to compile/execute, usually derived from {@link #indexFiles()}
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @throws IOException If an IO Exception occurs
     */
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        return compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerStream, BuildSettings.DEFAULT);
    }

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param mainGUI The main instance of MainGUI
     * @param imageClasses The {@link ImageClass}s to compile/execute, usually derived from {@link #indexFiles()}
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @param executeOverride The policy for executing or not
     * @throws IOException If an IO Exception occurs
     */
    public abstract CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException;

    /**
     * Gets all the {@link ImageClass}s that will be used during execution/compilation of the program with the current
     * language.
     * @return All the {@link ImageClass}s to be used during compilation/execution
     */
    public Optional<List<ImageClass>> indexFiles() {
        var LOGGER = getLogger();
        var mainGUI = this.startupLogic.getMainGUI();
        if (optionsNotFilled()) {
            LOGGER.error("Please select files for all options");
            mainGUI.setHaveError();
            return Optional.empty();
        }

        var lspWrapper = getLSPWrapper();
        var documentManager = lspWrapper.getDocumentManager();

        LOGGER.info("Reading {}'s DocumentManager index...", getName());

        var imageClasses = documentManager.getAllDocuments().stream().map(Document::getImageClass).collect(Collectors.toList());

        LOGGER.info("Found {} documents", imageClasses.size());

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
    protected void highlightAll(Option highlightDirectorySetting, List<ImageClass> imageClasses) throws IOException {
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
        return !getLanguageSettings().requiredFilled();
    }

    @Override
    public String toString() {
        return getName();
    }
}

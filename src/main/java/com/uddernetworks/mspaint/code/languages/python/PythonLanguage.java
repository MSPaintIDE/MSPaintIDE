package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.languages.HighlightData;
import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.code.languages.LanguageSettings;
import com.uddernetworks.mspaint.code.languages.Option;
import com.uddernetworks.mspaint.code.lsp.LSP;
import com.uddernetworks.mspaint.code.lsp.LanguageServerWrapper;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.StartupLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PythonLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(PythonLanguage.class);

    private LanguageSettings settings = new PythonSettings();
    private PythonCodeManager pythonCodeManager = new PythonCodeManager(this);
    private HighlightData highlightData = new PythonHighlightData();
    private LanguageServerWrapper lspWrapper = new LanguageServerWrapper(this.startupLogic, LSP.PYTHON,
            Collections.singletonList("pyls"));

    public PythonLanguage(StartupLogic startupLogic) {
        super(startupLogic);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getName() {
        return "Python";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {"py"};
    }

    @Override
    public Option getInputOption() {
        return PythonOptions.INPUT_DIRECTORY;
    }

    @Override
    public File getInputLocation() {
        return getLanguageSettings().getSetting(PythonOptions.INPUT_DIRECTORY);
    }

    @Override
    public File getAppOutput() {
        return getLanguageSettings().getSetting(PythonOptions.PROGRAM_OUTPUT);
    }

    @Override
    public File getCompilerOutput() {
        return getLanguageSettings().getSetting(PythonOptions.COMPILER_OUTPUT);
    }

    @Override
    public boolean isInterpreted() {
        return true;
    }

    @Override
    public LanguageServerWrapper getLSPWrapper() {
        return this.lspWrapper;
    }

    @Override
    public boolean hasLSP() {
        var output = Commandline.runSyncCommand("pip list");
        return output.contains("python-language-server ");
    }

    @Override
    public boolean installLSP() {
        if (hasLSP()) return false;

        try {
            var res = JOptionPane.showOptionDialog(null,
                    "Would you like to proceed with downloading the Python Language Server by palantir?",
                    "Download Confirm", 0, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(new File("E:\\MSPaintIDE\\src\\main\\resources\\icons\\popup\\save.png"))),
                    new String[]{"Yes", "No", "Website"}, "Yes");

            if (res == 0) {
                var output = Commandline.runSyncCommand("pip install python-language-server[all]");

                if (output.contains("'install_requires' must be")) {
                    Commandline.runSyncCommand("pip install -U setuptools");
                    output = Commandline.runSyncCommand("pip install python-language-server[all]");
                }

                if (output.contains("Successfully installed")) {
                    LOGGER.info("Successfully installed the Python Language Server");
                    return true;
                } else if (output.contains("is not recognized as an internal or external command")) {
                    LOGGER.error("You must have Python and pip installed on your system and in your PATH before installing");
                    return false;
                } else {
                    LOGGER.error("An unknown error caused the LSP to not be installed. The log is below:\n{}", output);
                }
            } else if (res == 1) {
            } else {
                Desktop.getDesktop().browse(new URI("https://github.com/palantir/python-language-server"));
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("There was an error while trying to install the Java LSP server", e);
        }

        return false;
    }

    @Override
    public boolean hasRuntime() {
        return Commandline.runSyncCommand("python --version").startsWith("Python ");
    }

    @Override
    public String downloadRuntimeLink() {
        return "https://www.python.org/downloads/";
    }

    @Override
    public HighlightData getHighlightData() {
        return this.highlightData;
    }

    @Override
    public LanguageSettings getLanguageSettings() {
        return this.settings;
    }

    @Override
    public void highlightAll(List<ImageClass> imageClasses) throws IOException {
        if (!this.settings.<Boolean>getSetting(PythonOptions.HIGHLIGHT)) return;
        highlightAll(PythonOptions.HIGHLIGHT_DIRECTORY, imageClasses);
    }

    @Override
    public Optional<List<ImageClass>> indexFiles() {
        return indexFiles(PythonOptions.INPUT_DIRECTORY);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        var imageClassesOptional = indexFiles();
        if (imageClassesOptional.isEmpty()) {
            LOGGER.error("Error while finding ImageClasses, aborting...");
            return new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        }

        return compileAndExecute(mainGUI, imageClassesOptional.get(), imageOutputStream, compilerStream);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) throws IOException {
        return compileAndExecute(mainGUI, imageClasses, imageOutputStream, compilerStream, BuildSettings.DEFAULT);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        return this.pythonCodeManager.executeCode(mainGUI, imageClasses, imageOutputStream, compilerStream);
    }
}

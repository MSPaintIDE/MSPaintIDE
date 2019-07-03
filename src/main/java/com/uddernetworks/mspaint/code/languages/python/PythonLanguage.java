package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.BuildSettings;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PythonLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(PythonLanguage.class);

    private LanguageSettings settings = new PythonSettings();
    private PythonCodeManager pythonCodeManager = new PythonCodeManager(this);
    private HighlightData highlightData = new PythonHighlightData(this);
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
    public Option getHighlightOption() {
        return PythonOptions.HIGHLIGHT_DIRECTORY;
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
        return Commandline.runSyncCommand("pip list").contains("python-language-server ");
    }

    @Override
    public boolean installLSP() {
        return lspInstallHelper("Would you like to proceed with downloading the Python Language Server by palantir?", "https://github.com/palantir/python-language-server", () -> {
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
            } else {
                LOGGER.error("An unknown error caused the LSP to not be installed. The log is below:\n{}", output);
            }

            return false;
        });
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
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        return this.pythonCodeManager.executeCode(mainGUI, imageClasses, imageOutputStream, compilerStream);
    }
}

package com.uddernetworks.mspaint.code.languages.javascript;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JSLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(JSLanguage.class);

    private LanguageSettings settings = new JSSettings();
    private JSCodeManager jsCodeManager = new JSCodeManager(this);
    private HighlightData highlightData = new JSHighlightData(this);
    private LanguageServerWrapper lspWrapper = new LanguageServerWrapper(this.startupLogic, LSP.JS, System.getenv("APPDATA") + "\\npm\\node_modules\\javascript-typescript-langserver\\lib",
            Arrays.asList("node", "language-server-stdio"))
                .writeOnChange();

    public JSLanguage(StartupLogic startupLogic) {
        super(startupLogic);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {"js"};
    }

    @Override
    public Option getInputOption() {
        return JSOptions.INPUT_DIRECTORY;
    }

    @Override
    public Option getHighlightOption() {
        return JSOptions.HIGHLIGHT_DIRECTORY;
    }

    @Override
    public File getAppOutput() {
        return getLanguageSettings().getSetting(JSOptions.PROGRAM_OUTPUT);
    }

    @Override
    public File getCompilerOutput() {
        return getLanguageSettings().getSetting(JSOptions.COMPILER_OUTPUT);
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
        var output = Commandline.runSyncCommand("cmd /c node \"%USERPROFILE%\\AppData\\Roaming\\npm\\node_modules\\javascript-typescript-langserver\\lib\\language-server-stdio\" --version");
        return !output.contains("Cannot find module") && Arrays.stream(output.split("\\.")).allMatch(StringUtils::isNumeric);
    }

    @Override
    public boolean installLSP() {
        return lspInstallHelper("Would you like to proceed with downloading the JavaScript Language Server by sourcegraph?", "https://www.npmjs.com/package/javascript-typescript-langserver", () -> {
            var output = Commandline.runSyncCommand("cmd /c npm install -g javascript-typescript-langserver");

            if (output.contains(" packages from ")) {
                LOGGER.info("Successfully installed the JavaScript Language Server");
                return true;
            } else if (output.contains("is not recognized as an internal or external command")) {
                LOGGER.error("You must have Node.js installed on your system and in your PATH before installing");
            } else {
                LOGGER.error("An unknown error caused the LSP to not be installed. The log is below:\n{}", output);
            }

            return false;
        });
    }

    @Override
    public boolean hasRuntime() {
        return Commandline.runSyncCommand("node --version").startsWith("v");
    }

    @Override
    public String downloadRuntimeLink() {
        return "https://nodejs.org/en/download/";
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
        if (!this.settings.<Boolean>getSetting(JSOptions.HIGHLIGHT)) return;
        highlightAll(JSOptions.HIGHLIGHT_DIRECTORY, imageClasses);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        return this.jsCodeManager.executeCode(mainGUI, imageClasses, imageOutputStream, compilerStream);
    }
}

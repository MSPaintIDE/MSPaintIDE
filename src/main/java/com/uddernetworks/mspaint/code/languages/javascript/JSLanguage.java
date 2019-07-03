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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class JSLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(JSLanguage.class);

    private LanguageSettings settings = new JSSettings();
    private JSCodeManager jsCodeManager = new JSCodeManager(this);
    private HighlightData highlightData = new JSHighlightData();
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
        if (hasLSP()) return false;

        try {
            var res = JOptionPane.showOptionDialog(null,
                    "Would you like to proceed with downloading the JavaScript Language Server by sourcegraph?",
                    "Download Confirm", 0, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(new File("E:\\MSPaintIDE\\src\\main\\resources\\icons\\popup\\save.png"))),
                    new String[]{"Yes", "No", "Website"}, "Yes");

            if (res == 0) {
                var output = Commandline.runSyncCommand("cmd /c npm install -g javascript-typescript-langserver");

                if (output.contains(" packages from ")) {
                    LOGGER.info("Successfully installed the JavaScript Language Server");
                    return true;
                } else if (output.contains("is not recognized as an internal or external command")) {
                    LOGGER.error("You must have Node.js installed on your system and in your PATH before installing");
                    return false;
                } else {
                    LOGGER.error("An unknown error caused the LSP to not be installed. The log is below:\n{}", output);
                }
            } else if (res == 1) {
            } else {
                Desktop.getDesktop().browse(new URI("https://www.npmjs.com/package/javascript-typescript-langserver"));
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("There was an error while trying to install the Python LSP server", e);
        }

        return false;
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

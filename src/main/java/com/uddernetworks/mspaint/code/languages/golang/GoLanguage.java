package com.uddernetworks.mspaint.code.languages.golang;

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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GoLanguage extends Language {

    private static Logger LOGGER = LoggerFactory.getLogger(GoLanguage.class);

    private LanguageSettings settings = new GoSettings();
    private GoCodeManager goCodeManager = new GoCodeManager(this);
    private HighlightData highlightData = new GoHighlightData();
    private LanguageServerWrapper lspWrapper = new LanguageServerWrapper(this.startupLogic, LSP.GO,
            Arrays.asList("gopls", "serve", "-logfile", "auto"))
            .setServerDirectorySupplier(() -> getStaticParent().orElse(new File("")).getAbsolutePath())
                .useInputAsWorkspace()
                .writeOnChange();

    public GoLanguage(StartupLogic startupLogic) {
        super(startupLogic);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getName() {
        return "Go";
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] {"go"};
    }

    @Override
    public Option getInputOption() {
        return GoOptions.INPUT_DIRECTORY;
    }

    @Override
    public Option getHighlightOption() {
        return GoOptions.HIGHLIGHT_DIRECTORY;
    }

    public static File getGoSrc() {
        return new File(System.getenv("GOPATH"), "src");
    }

    @Override
    public File getAppOutput() {
        return getLanguageSettings().getSetting(GoOptions.PROGRAM_OUTPUT);
    }

    @Override
    public File getCompilerOutput() {
        return getLanguageSettings().getSetting(GoOptions.COMPILER_OUTPUT);
    }

    @Override
    public boolean isInterpreted() {
        return false;
    }

    @Override
    public LanguageServerWrapper getLSPWrapper() {
        return this.lspWrapper;
    }

    @Override
    public Optional<File> getStaticParent() {
        return Optional.of(getGoSrc());
    }

    @Override
    public boolean hasLSP() {
        return Commandline.runSyncCommand("gopls version").contains(", built in");
    }

    @Override
    public boolean installLSP() {
        if (hasLSP()) return false;

        try {
            var res = JOptionPane.showOptionDialog(null,
                    "Would you like to proceed with downloading the Go Language Server by Google?",
                    "Download Confirm", 0, JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(ImageIO.read(new File("E:\\MSPaintIDE\\src\\main\\resources\\icons\\popup\\save.png"))),
                    new String[]{"Yes", "No", "Website"}, "Yes");

            if (res == 0) {
                var output = Commandline.runSyncCommand("go get golang.org/x/tools/gopls");

                if (hasLSP()) {
                    LOGGER.info("Successfully installed the Go Language Server");
                    return true;
                } else if (output.contains("is not recognized as an internal or external command")) {
                    LOGGER.error("You must have Go installed on your system and in your PATH before installing");
                    return false;
                } else {
                    LOGGER.error("An unknown error caused the LSP to not be installed. The log is below:\n{}", output);
                }
            } else if (res == 1) {
            } else {
                Desktop.getDesktop().browse(new URI("https://github.com/golang/go/wiki/gopls"));
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("There was an error while trying to install the Go LSP server", e);
        }

        return false;
    }

    @Override
    public boolean hasRuntime() {
        return Commandline.runSyncCommand("go version").startsWith("go version ");
    }

    @Override
    public String downloadRuntimeLink() {
        return "https://golang.org/dl/";
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
        if (!this.settings.<Boolean>getSetting(GoOptions.HIGHLIGHT)) return;
        highlightAll(GoOptions.HIGHLIGHT_DIRECTORY, imageClasses);
    }

    @Override
    public CompilationResult compileAndExecute(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, BuildSettings executeOverride) throws IOException {
        return this.goCodeManager.executeCode(mainGUI, imageOutputStream, compilerStream);
    }
}

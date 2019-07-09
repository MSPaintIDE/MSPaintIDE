package com.uddernetworks.mspaint.code.languages.javascript;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultRunningCode;
import com.uddernetworks.mspaint.code.languages.golang.GoCodeManager;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.logging.ThreadedLogger;
import com.uddernetworks.mspaint.main.MainGUI;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class JSCodeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GoCodeManager.class);
    private JSLanguage language;


    public JSCodeManager(JSLanguage language) {
        this.language = language;
    }

    public DefaultCompilationResult executeCode(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) {
        mainGUI.setIndeterminate(true);

        compilerStream.changeColor(Color.RED);
        PrintStream compilerOut = new PrintStream(compilerStream);
        PrintStream programOut = new PrintStream(imageOutputStream);

        ThreadedLogger.addPipe(compilerOut, "JSCompiler", JSCodeManager.class, Commandline.class);

        var runningFile = this.language.getLanguageSettings().<File>getSetting(JSOptions.RUNNING_FILE);
        var runningSrc = new AtomicReference<File>();

        // Puts .java files into this directory, which are then compiled from here
        var generateJava = new File(System.getProperty("java.io.tmpdir"), "MSPaintIDE_" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        var genSrc = new File(generateJava, "src");

        var input = this.language.getLanguageSettings().<File>getSetting(JSOptions.INPUT_DIRECTORY);
        imageClasses.forEach(imageClass -> {
            var relative = input.toURI().relativize(imageClass.getInputImage().toURI());
            var absoluteOutput = new File(genSrc, relative.getPath().replaceAll("\\.png$", ""));

            if (imageClass.getInputImage().equals(runningFile)) runningSrc.set(absoluteOutput);
            try {
                FileUtils.write(absoluteOutput, imageClass.getText(), Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("An error occurred while writing to the temp file {}", absoluteOutput.getAbsolutePath());
            }
        });

        LOGGER.info("Executing...");
        mainGUI.setStatusText("Executing...");
        final var programStart = System.currentTimeMillis();

        if (runningSrc.get() == null) {
            LOGGER.error("Couldn't locate file to run {}", runningFile);
            return new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        }

        var runningCodeManager = mainGUI.getStartupLogic().getRunningCodeManager();
        runningCodeManager.runCode(new DefaultRunningCode(() -> {
            ThreadedLogger.removePipe("JSCompiler");
            ThreadedLogger.addPipe(programOut, "JSProgram", JSCodeManager.class, Commandline.class);

            return Commandline.runLiveCommand(Arrays.asList("node", runningSrc.get().getAbsolutePath()), genSrc, "Node");
        }).afterSuccess(exitCode -> {
            if (exitCode < 0) {
                LOGGER.info("Forcibly terminated after " + (System.currentTimeMillis() - programStart) + "ms");
            } else {
                LOGGER.info("Executed " + (exitCode > 0 ? "with errors " : "") + "in " + (System.currentTimeMillis() - programStart) + "ms");
            }
        }).afterError(message -> {
            LOGGER.info("Program stopped for the reason: " + message);
        }).afterAll((exitCode, ignored) -> {
            mainGUI.setStatusText("");
            ThreadedLogger.removePipe("JSProgram");
        }));

        return new DefaultCompilationResult(CompilationResult.Status.RUNNING);
    }
}

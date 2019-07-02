package com.uddernetworks.mspaint.code.languages.python;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.languages.java.JavaCodeManager;
import com.uddernetworks.mspaint.code.languages.java.JavaRunningCode;
import com.uddernetworks.mspaint.imagestreams.ConsoleManager;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
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

public class PythonCodeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(JavaCodeManager.class);
    private PythonLanguage language;


    public PythonCodeManager(PythonLanguage language) {
        this.language = language;
    }

    public DefaultCompilationResult executeCode(MainGUI mainGUI, List<ImageClass> imageClasses, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) {
        mainGUI.setIndeterminate(true);

        compilerStream.changeColor(Color.RED);
        PrintStream compilerOut = new PrintStream(compilerStream);
        PrintStream programOut = new PrintStream(imageOutputStream);

        ConsoleManager.setAll(new PrintStream(compilerOut));

        var runningFile = this.language.getLanguageSettings().getSetting(PythonOptions.RUNNING_FILE);
        var runningSrc = new AtomicReference<File>();

        // Puts .java files into this directory, which are then compiled from here
        var generateJava = new File(System.getProperty("java.io.tmpdir"), "MSPaintIDE_" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        var genSrc = new File(generateJava, "src");

        var input = this.language.getLanguageSettings().<File>getSetting(PythonOptions.INPUT_DIRECTORY);
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

        var runningCodeManager = mainGUI.getStartupLogic().getRunningCodeManager();
        runningCodeManager.runCode(new JavaRunningCode(() -> {
            ConsoleManager.setAll(programOut);
            return Commandline.runLiveCommand(Arrays.asList("python", runningSrc.get().getAbsolutePath()), genSrc);
        }).afterSuccess(exitCode -> {
            if (exitCode < 0) {
                LOGGER.info("Forcibly terminated after " + (System.currentTimeMillis() - programStart) + "ms");
            } else {
                LOGGER.info("Executed " + (exitCode > 0 ? "with errors " : "") + "in " + (System.currentTimeMillis() - programStart) + "ms");
            }
        }).afterError(message -> {
            info("Program stopped for the reason: " + message);
        }).afterAll((exitCode, ignored) -> {
            mainGUI.setStatusText("");
        }));

        return new DefaultCompilationResult(CompilationResult.Status.RUNNING);
    }

    private void info(String message) {
        LOGGER.info(message);
        System.out.println(message);
    }
}

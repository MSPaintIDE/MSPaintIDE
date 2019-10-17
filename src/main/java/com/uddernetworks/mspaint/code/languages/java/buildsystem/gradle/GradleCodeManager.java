package com.uddernetworks.mspaint.code.languages.java.buildsystem.gradle;

import com.uddernetworks.mspaint.cmd.Commandline;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultRunningCode;
import com.uddernetworks.mspaint.code.languages.SourceMover;
import com.uddernetworks.mspaint.code.languages.java.JavaCodeManager;
import com.uddernetworks.mspaint.code.languages.java.JavaLangOptions;
import com.uddernetworks.mspaint.code.languages.java.JavaLanguage;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.logging.ThreadedLogger;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.project.ProjectManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradleCodeManager extends JavaCodeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GradleCodeManager.class);

    private JavaLanguage language;

    private GradleConnector gradleConnector;

    public GradleCodeManager(JavaLanguage language) {
        super(language);
    }

    @Override
    public CompilationResult compileAndExecute(List<ImageClass> imageClasses, File jarFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        gradleConnector = new GradleConnector(ProjectManager.getPPFProject().getFile().getParent());

        mainGUI.setIndeterminate(true);
        classOutputFolder.mkdirs();

        compilerStream.changeColor(Color.RED);
        var compilerOut = new PrintStream(compilerStream);
        var programOut = new PrintStream(imageOutputStream);

        ThreadedLogger.addPipe(compilerOut, "JavaCompiler", GradleCodeManager.class, Commandline.class);

        long start = System.currentTimeMillis();

        mainGUI.setStatusText("Compiling...");

        LOGGER.info("Compiling {} files", imageClasses.size());

        var sourceMover = new SourceMover(language.getLanguageSettings().getSetting(JavaLangOptions.INPUT_DIRECTORY));
        sourceMover.moveToHardTemp(imageClasses);

        jarFile.delete();
        FileUtils.deleteDirectory(classOutputFolder);
        classOutputFolder.mkdirs();

        gradleConnector.runTask("build");

        LOGGER.info("Compiled in " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        LOGGER.info("Packaging jar...");
        mainGUI.setStatusText("Packaging jar...");

        var jarCreate = new ArrayList<String>();
        jarCreate.add("jar");
        jarCreate.add("-c");
        jarCreate.add("-f");
        jarCreate.add(jarFile.getAbsolutePath());
        jarCreate.add("-e");
        jarCreate.add(this.language.getLanguageSettings().getSetting(JavaLangOptions.MAIN));
        jarCreate.add("*");
        Commandline.runLiveCommand(jarCreate, classOutputFolder, "Jar");

        LOGGER.info("Packaged jar in " + (System.currentTimeMillis() - start) + "ms");

        if (!execute) {
            return new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        }

        LOGGER.info("Executing...");
        mainGUI.setStatusText("Executing...");
        final var programStart = System.currentTimeMillis();

        var runningCodeManager = mainGUI.getStartupLogic().getRunningCodeManager();
        runningCodeManager.runCode(new DefaultRunningCode(() -> {
            ThreadedLogger.removePipe("JavaCompiler");
            ThreadedLogger.addPipe(programOut, "JavaProgram", GradleCodeManager.class, Commandline.class);

            return Commandline.runLiveCommand(Arrays.asList("java", "-jar", jarFile.getAbsolutePath()), null, "Java");
        }).afterSuccess(exitCode -> {
            if (exitCode < 0) {
                LOGGER.info("Forcibly terminated after " + (System.currentTimeMillis() - programStart) + "ms");
            } else {
                LOGGER.info("Executed " + (exitCode > 0 ? "with errors " : "") + "in " + (System.currentTimeMillis() - programStart) + "ms");
            }
        }).afterError(message -> LOGGER.info("Program stopped for the reason: " + message)).afterAll((exitCode, ignored) -> {
            mainGUI.setStatusText("");
            ThreadedLogger.removePipe("JavaProgram");
        }));

        return new DefaultCompilationResult(CompilationResult.Status.RUNNING);
    }

    private static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {

            if (!dest.exists()) dest.mkdir();

            for (String file : src.list()) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }
        } else {
            try (InputStream in = new FileInputStream(src)) {
                try (OutputStream out = new FileOutputStream(dest)) {

                    byte[] buffer = new byte[1024];

                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            }
        }
    }
}
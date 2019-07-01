package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.execution.CompilationResult;
import com.uddernetworks.mspaint.code.execution.DefaultCompilationResult;
import com.uddernetworks.mspaint.imagestreams.ConsoleManager;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class JavaCodeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(JavaCodeManager.class);

    private JavaLanguage language;

    public JavaCodeManager(JavaLanguage language) {
        this.language = language;
    }

    // TODO: Multi-thread this
    public CompilationResult compileAndExecute(List<ImageClass> imageClasses, File jarFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        mainGUI.setIndeterminate(true);
        classOutputFolder.mkdirs();

        compilerStream.changeColor(Color.RED);
        PrintStream compilerOut = new PrintStream(compilerStream);
        PrintStream programOut = new PrintStream(imageOutputStream);

        ConsoleManager.setAll(new PrintStream(compilerOut));

        long start = System.currentTimeMillis();

        info("Compiling...");
        mainGUI.setStatusText("Compiling...");

        LOGGER.info("Compiling {} files", imageClasses.size());

        // Puts .java files into this directory, which are then compiled from here
        var generateJava = new File(System.getProperty("java.io.tmpdir"), "MSPaintIDE_" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        var genSrc = new File(generateJava, "src");

        var javaFiles = new ArrayList<File>();
        var input = this.language.getLanguageSettings().<File>getSetting(JavaOptions.INPUT_DIRECTORY);
        imageClasses.forEach(imageClass -> {
            var relative = input.toURI().relativize(imageClass.getInputImage().toURI());
            var absoluteOutput = new File(genSrc, relative.getPath().replaceAll("\\.png$", ""));

            javaFiles.add(absoluteOutput);
            try {
                FileUtils.write(absoluteOutput, imageClass.getText(), Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("An error occurred while writing to the temp file {}", absoluteOutput.getAbsolutePath());
            }
        });

        jarFile.delete();
        FileUtils.deleteDirectory(classOutputFolder);
        classOutputFolder.mkdirs();

        var javac = new ArrayList<String>();
        javac.add("javac");
        javac.add("-g");
        javac.add("-verbose");
        if (!libs.isEmpty()) javac.add("-cp");
        libs.forEach(file -> {
            javac.add(file.getAbsolutePath());
        });
        javac.add("-d");
        javac.add(classOutputFolder.getAbsolutePath());
        javaFiles.forEach(file -> javac.add(file.getAbsolutePath()));
        runCommand(javac);

        LOGGER.info("Compiled in " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        LOGGER.info("Packaging jar...");
        mainGUI.setStatusText("Packaging jar...");

        if (otherFiles != null) {
            if (otherFiles.isDirectory()) {
                copyFolder(otherFiles, classOutputFolder);
            } else {
                File newLoc = new File(classOutputFolder, otherFiles.getName());
                newLoc.createNewFile();
                Files.copy(Paths.get(otherFiles.getAbsolutePath()), Paths.get(newLoc.getAbsolutePath()), REPLACE_EXISTING);
            }
        }

        var jarCreate = new ArrayList<String>();
        jarCreate.add("jar");
        jarCreate.add("-c");
        jarCreate.add("-f");
        jarCreate.add(jarFile.getAbsolutePath());
        jarCreate.add("-e");
        jarCreate.add(this.language.getLanguageSettings().getSetting(JavaOptions.MAIN));
        jarCreate.add("*");
        runCommand(jarCreate, classOutputFolder);

        LOGGER.info("Packaged jar in " + (System.currentTimeMillis() - start) + "ms");

        if (!execute) {
            return new DefaultCompilationResult(CompilationResult.Status.COMPILE_COMPLETE);
        }

        LOGGER.info("Executing...");
        mainGUI.setStatusText("Executing...");
        final var programStart = System.currentTimeMillis();

        var runningCodeManager = mainGUI.getStartupLogic().getRunningCodeManager();
        runningCodeManager.runCode(new JavaRunningCode(() -> {
            ConsoleManager.setAll(programOut);
            return runCommand(Arrays.asList("java", "-jar", jarFile.getAbsolutePath()));
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

    private int runCommand(List<String> command) {
        return runCommand(command, null);
    }

    private int runCommand(List<String> command, File directory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            processBuilder.redirectError();
            processBuilder.inheritIO();
            if (directory != null) processBuilder.directory(directory);
            Process p = processBuilder.start();
            InputStreamConsumer streamConsumer = new InputStreamConsumer(p.getInputStream());
            streamConsumer.start();
            int exitCode = p.waitFor();
            streamConsumer.join();
            LOGGER.info("Process terminated with {}", exitCode);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("An error occurred while running command with arguments " + command, e);
            return -1;
        }
    }

    public static class InputStreamConsumer extends Thread {

        private InputStream inputStream;
        private StringBuilder line = new StringBuilder();

        public InputStreamConsumer(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                int intVal = -1;
                while ((intVal = inputStream.read()) != -1) {
                    char value = (char) intVal;
                    if (value == '\n') {
                        LOGGER.info(line.toString());
                        line = new StringBuilder();
                    } else {
                        line.append(value);
                    }
                }
            } catch (IOException exp) {
                exp.printStackTrace();
            }

        }
    }
}
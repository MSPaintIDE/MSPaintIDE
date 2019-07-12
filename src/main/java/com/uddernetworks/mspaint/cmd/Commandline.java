package com.uddernetworks.mspaint.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Commandline {

    private static Logger LOGGER = LoggerFactory.getLogger(Commandline.class);

    public static String runCommand(List<String> command) {
        return runCommand(false, command);
    }

    public static String runCommand(String... command) {
        return runCommand(false, Arrays.asList(command));
    }

    public static String runCommand(boolean cmdCPrefix, String... command) {
        return runCommand(cmdCPrefix, Arrays.asList(command));
    }

    public static String runCommand(boolean cmdCPrefix, List<String> command) {
        if (cmdCPrefix) {
            var temp = new ArrayList<>(Arrays.asList("cmd", "/c"));
            temp.addAll(command);
            command = temp;
        }
        var result = new StringBuilder();
        runInheritedCommand(command, null, false, process -> {
            inheritIOToStringBuilder(process.getInputStream(), result);
            inheritIOToStringBuilder(process.getErrorStream(), result);
        });
        return result.toString();
    }

    public static int runLiveCommand(List<String> command) {
        return runLiveCommand(command, null);
    }

    public static int runLiveCommand(List<String> command, String threadName) {
        return runLiveCommand(command, null, threadName);
    }

    public static int runLiveCommand(List<String> command, File directory, String threadName) {
        return runInheritedCommand(command, directory, true, process -> {
            inheritIO(process.getInputStream(), threadName);
            inheritIO(process.getErrorStream(), threadName);
        });
    }

    public static int runInheritedCommand(List<String> command, File directory, boolean printStatus, Consumer<Process> processCreate) {
        LOGGER.info("Running command {}", String.join(" ", command));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (directory != null) processBuilder.directory(directory);
            Process process = processBuilder.start();

            processCreate.accept(process);

            var exitCode = 1;
            Runtime.getRuntime().addShutdownHook(new Thread(process::destroyForcibly));

            try {
                exitCode = process.waitFor();
            } catch (InterruptedException ignored) { // This is probably from manually stopping the process; nothing bad to report
                process.destroyForcibly();
            }

            if (printStatus) LOGGER.info("Process terminated with {}", exitCode);
            return exitCode;
        } catch (IOException e) {
            if (!e.getLocalizedMessage().contains("The system cannot find the file specified")) LOGGER.error("An error occurred while running command with arguments " + command, e);
            return -1;
        }
    }

    private static void inheritIO(InputStream inputStream, String threadName) {
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setName(threadName);
            Scanner sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                LOGGER.info(sc.nextLine());
            }
        });
    }

    private static void inheritIOToStringBuilder(InputStream inputStream, StringBuilder stringBuilder) {
        CompletableFuture.runAsync(() -> {
            Scanner sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                var line = sc.nextLine();
                synchronized (stringBuilder) {
                    stringBuilder.append(line);
                }
            }
        });
    }
}

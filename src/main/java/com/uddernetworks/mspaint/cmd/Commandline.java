package com.uddernetworks.mspaint.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Commandline {

    private static Logger LOGGER = LoggerFactory.getLogger(Commandline.class);

    public static String runSyncCommand(String command) {
        return runSyncCommand(command, new File("C:\\Windows\\System32"));
    }

    public static String runSyncCommand(String command, File directory) {
        String[] out = {null};
        Commandline.runCommand(command, false, directory, res -> out[0] = res);
        return out[0];
    }

    public static void runCommand(String command, boolean async, File directory, Consumer<String> result) {
        Runnable commandRunnable = () -> {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command, null, directory);

                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                }

                result.accept(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                result.accept("");
            }
        };

        if (async) {
            CompletableFuture.runAsync(commandRunnable);
        } else {
            commandRunnable.run();
        }
    }

    public static int runLiveCommand(List<String> command) {
        return runLiveCommand(command, null);
    }

    public static int runLiveCommand(List<String> command, File directory) {
        return runLiveCommand(command, directory, "");
    }

    public static int runLiveCommand(List<String> command, File directory, String threadName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectError();
            processBuilder.redirectOutput();
            if (directory != null) processBuilder.directory(directory);
            Process process = processBuilder.start();
            InputStreamConsumer streamConsumer = new InputStreamConsumer(process.getInputStream(), LOGGER);
            streamConsumer.setName(threadName);
            streamConsumer.start();
            var exitCode = 1;
            try {
                exitCode = process.waitFor();
                streamConsumer.join();
            } catch (InterruptedException ignored) { // This is probably from manually stopping the process; nothing bad to report
                process.destroyForcibly();
            }

            LOGGER.info("Process terminated with {}", exitCode);
            return exitCode;
        } catch (IOException e) {
            LOGGER.error("An error occurred while running command with arguments " + command, e);
            return -1;
        }
    }

}

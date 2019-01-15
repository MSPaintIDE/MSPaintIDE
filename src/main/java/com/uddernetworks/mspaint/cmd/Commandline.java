package com.uddernetworks.mspaint.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Commandline {

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

}

package com.uddernetworks.mspaint.code.lsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class BetterProvider {

    private static Logger LOGGER = LoggerFactory.getLogger(BetterProvider.class);

    private List<String> commands;
    private String workingDir;

    public BetterProvider(List<String> commands, String workingDir) {
        this.commands = commands;
        this.workingDir = workingDir;
    }

    private Process process = null;

    public void start() throws IOException {
        if (workingDir == null || commands == null || commands.isEmpty() || commands.contains(null)) {
            throw new IOException("Unable to start language server: " + this.toString());
        }
        ProcessBuilder builder = createProcessBuilder();
        LOGGER.info("Starting server process with commands " + commands + " and workingDir " + workingDir);
        process = builder.start();
        if (!process.isAlive()) {
            throw new IOException("Unable to start language server: " + this.toString());
        } else {
            LOGGER.info("Server process started " + process);
        }

//        final var pid = process.pid();
//
////        Runtime.getRuntime().addShutdownHook(new Thread(process::destroyForcibly));
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                var cmd = "taskkill /F /PID " + pid;
//                System.out.println("Kill " + pid + " via " + cmd);
//                Runtime.getRuntime().exec(cmd);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }));
    }

    private ProcessBuilder createProcessBuilder() {
        //TODO for cquery, REMOVE
        commands.forEach(c -> c = c.replace("\'", ""));
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(new File(workingDir));
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        return builder;
    }

    public InputStream getInputStream() {
        return process != null ? process.getInputStream() : null;
    }

    public OutputStream getOutputStream() {
        return process != null ? process.getOutputStream() : null;
    }

    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BetterProvider) {
            BetterProvider other = (BetterProvider) obj;
            return commands.size() == other.commands.size() && new HashSet<>(commands) == new HashSet<>(other.commands)
                    && workingDir.equals(other.workingDir);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commands) ^ Objects.hashCode(workingDir);
    }
}
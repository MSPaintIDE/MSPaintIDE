package com.uddernetworks.mspaint.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GitController {

    private MainGUI mainGUI;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public GitController(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    private void runCommand(String command, Consumer<String> result) {
        runCommand(command, true, result);
    }

    private void runCommand(String command, boolean showStatus, Consumer<String> result) {
        this.mainGUI.setIndeterminate(true);
        if (showStatus) this.mainGUI.setStatusText("Running command '" + command + "'");
        System.out.println(command);
        executorService.execute(() -> {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);

                String line;
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = input.readLine()) != null) {
                    stringBuilder.append(line);
                }

                input.close();

                this.mainGUI.updateLoading(0, 1);
                this.mainGUI.setStatusText(null);
                result.accept(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void getVersion(Consumer<String> result) {
        this.mainGUI.setStatusText("Checking git version");
        runCommand("git --version", false, versionResult -> result.accept(versionResult.toLowerCase().contains("git version") ? versionResult.substring(12) : null));
    }

    public void gitInit(File directory) {
        this.mainGUI.setIndeterminate(true);
        this.mainGUI.setStatusText("Creating local git repository");
        if (directory.isFile()) directory = directory.getParentFile();
        File gitFolder = new File(directory, "git");
        gitFolder.mkdirs();
        System.out.println(gitFolder.getAbsolutePath());
        runCommand("git init \"" + gitFolder.getAbsolutePath() + "\"", false, result -> { //  & git init
            System.out.println("result = " + result);

            this.mainGUI.updateLoading(0, 1);
            this.mainGUI.setStatusText(null);

            if (result.contains("Initialized")) {
                System.out.println("Created a local git repository.");
            } else {
                this.mainGUI.setHaveError();

                System.out.println("Unexpected git command result: \n" + result + "\nIf this looks correct, please make an issue on the MS Paint IDE GitHub: https://github.com/RubbaBoy/MSPaintIDE/issues/new");
            }
        });
    }
}

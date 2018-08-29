package com.uddernetworks.mspaint.git;

import com.google.gson.Gson;
import com.uddernetworks.mspaint.main.ImageClass;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.ocr.ImageIndex;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitController {

    private MainGUI mainGUI;
    private Gson gson = new Gson();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public GitController(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    private void runCommand(String command, File directory, Consumer<String> result) {
        runCommand(command, true, directory, result);
    }

    private void runCommand(String command, boolean showStatus, File directory, Consumer<String> result) {
        this.mainGUI.setIndeterminate(true);
        if (showStatus) this.mainGUI.setStatusText("Running command '" + command + "'");
        System.out.println((directory != null ? directory.getAbsolutePath() : "") + "> " + command);
        executorService.execute(() -> {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command, null, directory);

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

    private File getGitFolder() {
        File directory = new File(this.mainGUI.getMain().getInputImage());
        if (directory.isFile()) directory = directory.getParentFile();
        return new File(directory, "git");
    }

    private File getGitIndexFile() {
        return new File(getGitFolder(), "gitindex");
    }

    private GitIndex getGitIndex() throws IOException {
        File gitIndexFile = getGitIndexFile();
        if (!gitIndexFile.exists()) {
            gitIndexFile.createNewFile();
            return new GitIndex(new HashMap<>());
        }

        String str = new String(Files.readAllBytes(gitIndexFile.toPath()));

        return this.gson.fromJson(str, GitIndex.class);
    }

    public void getVersion(Consumer<String> result) {
        this.mainGUI.setStatusText("Checking git version");
        runCommand("git --version", false, null, versionResult -> result.accept(versionResult.toLowerCase().contains("git version") ? versionResult.substring(12) : null));
    }

    public void gitInit(File directory) {
        this.mainGUI.setIndeterminate(true);
        this.mainGUI.setStatusText("Creating local git repository");
        if (directory.isFile()) directory = directory.getParentFile();
        File gitFolder = new File(directory, "git");
        gitFolder.mkdirs();
        runCommand("git init", false, gitFolder, result -> { //  & git init
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

    public void addFiles(File[] files) throws IOException {
        GitIndex gitIndex = getGitIndex();
        Main main = this.mainGUI.getMain();

        ImageIndex imageIndex = new ImageIndex(new File(main.getLetterDirectory()));
        Map<String, BufferedImage> images = imageIndex.index();

        List<File> imageFiles = Arrays.stream(files).flatMap(file -> {
            if (file.isDirectory()) return main.getFilesFromDirectory(file, "png").stream();
            return Stream.of(file);
        }).collect(Collectors.toList());

        imageFiles.forEach(file -> {
            try {
                ImageClass imageClass = new ImageClass(file, new File(main.getObjectFile()), mainGUI, images, this.mainGUI.shouldUseProbe(), this.mainGUI.shouldUseCaches(), this.mainGUI.shouldSaveCaches());
                String rel = getRelativeClass(file).replace(".png", ".java");
                File scannedText = new File(getGitFolder() + File.separator + rel);
                scannedText.getParentFile().mkdirs();
                scannedText.createNewFile();
                Files.write(scannedText.toPath(), imageClass.getText().getBytes());
                gitIndex.addFile(file, scannedText);

                runCommand("git add \"" + scannedText.getAbsolutePath().replace("\\", "\\\\") + "\"", getGitFolder(), result -> System.out.println("Finished adding " + rel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.write(getGitIndexFile().toPath(), this.gson.toJson(gitIndex).getBytes());
    }

    private String getRelativeClass(File file) {
        Main main = this.mainGUI.getMain();
        File inputImage = new File(main.getInputImage());
        if (inputImage.isFile()) {
            try {
                throw new Exception("Tried to get relative class not in the input image path!");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }


        return inputImage.toURI().relativize(file.toURI()).getPath().replace("/", File.separator);
    }
}

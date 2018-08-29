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
import java.nio.file.StandardCopyOption;
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

    private void runCommand(String command, boolean async, File directory, Consumer<String> result) {
        runCommand(command, async, true, directory, result);
    }

    private void runCommand(String command, boolean async, boolean showStatus, File directory, Consumer<String> result) {
        this.mainGUI.setIndeterminate(true);
        if (showStatus) this.mainGUI.setStatusText("Running command '" + command + "'");
        System.out.println((directory != null ? directory.getAbsolutePath() : "") + " > " + command);
        Runnable commandRunnable = () -> {
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
        };

        if (async) {
            executorService.execute(commandRunnable);
        } else {
            commandRunnable.run();
        }
    }

    private File gitFolder = null;

    private File getGitFolder() {
        if (gitFolder != null) return gitFolder;
        File directory = new File(this.mainGUI.getMain().getInputImage());
        if (directory.isFile()) directory = directory.getParentFile();
        return gitFolder = new File(directory, "git");
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
        runCommand("git --version", true, false, null, versionResult -> result.accept(versionResult.toLowerCase().contains("git version") ? versionResult.substring(12) : null));
    }

    public void gitInit(File directory) {
        this.mainGUI.setIndeterminate(true);
        this.mainGUI.setStatusText("Creating local git repository");
        if (directory.isFile()) directory = directory.getParentFile();
        File gitFolder = new File(directory, "git");
        gitFolder.mkdirs();
        runCommand("git init", true, false, gitFolder, result -> { //  & git init
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
            if (file.isDirectory()) return main.getFilesFromDirectory(file, null).stream();
            return Stream.of(file);
        }).collect(Collectors.toList());

        imageFiles.forEach(file -> {
            try {
                File addingFile;
                String relative = getRelativeClass(file);
                if (file.getName().endsWith(".png")) {
                    ImageClass imageClass = new ImageClass(file, new File(main.getObjectFile()), mainGUI, images, this.mainGUI.shouldUseProbe(), this.mainGUI.shouldUseCaches(), this.mainGUI.shouldSaveCaches());
                    relative = relative.replace(".png", ".java");
                    addingFile = new File(getGitFolder() + File.separator + relative);
                    addingFile.getParentFile().mkdirs();
                    addingFile.createNewFile();
                    Files.write(addingFile.toPath(), imageClass.getText().getBytes());
                    gitIndex.addFile(file, addingFile);
                } else {
                    addingFile = new File(getGitFolder() + File.separator + relative);
                    addingFile.getParentFile().mkdirs();

                    Files.copy(file.toPath(), addingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                String finalRelative = relative;
                runCommand("git add \"" + addingFile.getAbsolutePath().replace("\\", "\\\\") + "\"", false, getGitFolder(), result -> System.out.println("Finished adding " + finalRelative));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Files.write(getGitIndexFile().toPath(), this.gson.toJson(gitIndex).getBytes());

        System.out.println("Finished adding " + imageFiles.size() + " file" + (imageFiles.size() > 1 ? "s" : ""));
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

    public void setRemoteOrigin(String url) {
        if (url == null || "".equals(url)) {
            System.out.println("Error: No URL for remote origin found");
            this.mainGUI.setHaveError();
            return;
        }

        runCommand("git remote -v", true, getGitFolder(), result -> {
            Runnable addOrigin = () -> runCommand("git remote add origin " + url, true, getGitFolder(), lastResult -> {
                if (result.contains("remote origin already exists")) {
                    System.out.println("Couldn't add as a remote origin.");
                } else {
                    System.out.println("Added " + url + " as a remote origin");
                }
            });

            if (result.contains("origin")) {
                runCommand("git remote remove origin", true, getGitFolder(), ignored -> addOrigin.run());
            } else {
                addOrigin.run();
            }
        });
    }

    public void commit(String message) {
        if (message == null || "".equals(message)) {
            System.out.println("Error: No commit message found");
            this.mainGUI.setHaveError();
            return;
        }

        runCommand("git commit -a -m \"" + message + "\"", true, getGitFolder(), result -> {
            if (result.contains("changed")) {
                System.out.println("Successfully committed");
            } else if (result.contains("nothing added")) {
                System.out.println("Nothing changed; couldn't commit");
            } else {
                System.out.println("Unexpected git command result: \n" + result + "\nIf this looks correct, please make an issue on the MS Paint IDE GitHub: https://github.com/RubbaBoy/MSPaintIDE/issues/new");
            }
        });
    }

    public void hasUnpushedCommits(Consumer<Boolean> result) {
        runCommand("git log origin/master..HEAD", true, getGitFolder(), logResult -> result.accept(logResult.contains("commit")));
    }

    public void push() {
        hasUnpushedCommits(hasUnpushed -> {
            if (!hasUnpushed) {
                System.out.println("Error: There are no unpushed commits to push");
                return;
            }

            runCommand("git push origin master", true, getGitFolder(), pushResult -> {
                if (pushResult.contains("does not appear to be a git repository")) {
                    System.out.println("Error: No origin set up, couldn't push");
                    return;
                }

                hasUnpushedCommits(stillHasUnpushed -> {
                    if (stillHasUnpushed) {
                        this.mainGUI.setHaveError();
                        System.out.println("Error: The push did not succeed, did your remote origin contain credentials? If not, please add them");
                    } else {
                        System.out.println("Pushed commits successfully");
                    }
                });
            });
        });

    }
}

package com.uddernetworks.mspaint.git;

import com.google.gson.Gson;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import com.uddernetworks.mspaint.main.ModifiedDetector;
import com.uddernetworks.mspaint.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitController {

    private static Logger LOGGER = LoggerFactory.getLogger(GitController.class);

    private MainGUI mainGUI;
    private Gson gson = new Gson();
    private Map<String, BufferedImage> images;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Map<String, Function<String, String>> commandOutputModifiers = new HashMap<>();
    private boolean hideOrigin;
    private File gitIndexFile = null;

    public GitController(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    private void runCommand(String command, boolean async, File directory, Consumer<String> result) {
        runCommand(command, async, true, directory, result);
    }

    private void runCommand(String command, boolean async, boolean showStatus, File directory, Consumer<String> result) {
        this.mainGUI.setIndeterminate(true);
        AtomicReference<String> safeCommand = new AtomicReference<>(command);
        commandOutputModifiers.forEach((functionName, stringStringFunction) -> safeCommand.set(stringStringFunction.apply(safeCommand.get())));

        if (showStatus) this.mainGUI.setStatusText("Running command '" + safeCommand.get() + "'");
        LOGGER.info((directory != null ? directory.getAbsolutePath() : "") + " > " + safeCommand.get());
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

                this.mainGUI.updateLoading(0, 1);
                this.mainGUI.setStatusText(null);
                result.accept(stringBuilder.toString());
            } catch (IOException e) {
                if (e.getMessage().contains("cannot find")) {
                    this.mainGUI.updateLoading(0, 1);
                    this.mainGUI.setStatusText(null);

                    result.accept("");
                    return;
                }

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
        File directory = ProjectManager.getPPFProject().getInputLocation().getParentFile();
        return gitFolder = new File(directory, "git");
    }

    private File getGitIndexFile() {
        return this.gitIndexFile == null ? (this.gitIndexFile = new File(getGitFolder(), "gitindex")) : this.gitIndexFile;
    }

    private GitIndex getGitIndex() throws IOException {
        File gitIndexFile = getGitIndexFile();
        if (!gitIndexFile.exists()) return new GitIndex(new HashMap<>());

        String str = new String(Files.readAllBytes(gitIndexFile.toPath()));

        return str.isEmpty() ? new GitIndex(new HashMap<>()) : this.gson.fromJson(str, GitIndex.class);
    }

    public void getVersion(Consumer<String> result) {
        this.mainGUI.setStatusText("Checking git version");
        runCommand("git --version", true, false, null, versionResult -> result.accept(versionResult.toLowerCase().contains("git version") ? versionResult.substring(12) : null));
    }

    public void gitInit(File directory) {
        this.mainGUI.setIndeterminate(true);
        this.mainGUI.setStatusText("Creating local git repository");
        directory = directory.getParentFile();
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

        List<File> imageFiles = Arrays.stream(files).flatMap(file -> {
            if (file.isDirectory()) return main.getFilesFromDirectory(file, (String[]) null).stream();
            return Stream.of(file);
        }).collect(Collectors.toList());

        imageFiles.forEach(file ->
                moveOrScan(file, null, true, gitIndex, (addingFile, relative) ->
                        runCommand("git add \"" + addingFile.getAbsolutePath().replace("\\", "\\\\") + "\"", false, getGitFolder(), result ->
                                LOGGER.info("Finished adding " + relative))));

        if (!getGitIndexFile().exists()) getGitIndexFile().createNewFile();
        Files.write(getGitIndexFile().toPath(), this.gson.toJson(gitIndex).getBytes());

        LOGGER.info("Finished adding " + imageFiles.size() + " file" + (imageFiles.size() > 1 ? "s" : ""));
    }

    private void moveOrScan(File file, File source, boolean addToIndex, GitIndex gitIndex, BiConsumer<File, String> result) {
        try {
            File addingFile;
            String relative = getRelativeClass(file);
            if (file.getName().endsWith(".png")) {
                ImageClass imageClass = new ImageClass(file, mainGUI, true, ProjectManager.getPPFProject().isUseCaches(), ProjectManager.getPPFProject().isSaveCaches());
                relative = relative.replace(".png", ".java");

                if (source == null) {
                    addingFile = new File(getGitFolder() + File.separator + relative);
                    addingFile.getParentFile().mkdirs();
                    addingFile.createNewFile();
                } else {
                    addingFile = source;
                }

                Files.write(addingFile.toPath(), imageClass.getText().getBytes());
                if (addToIndex) gitIndex.addFile(file, addingFile);
            } else {
                if (source == null) {
                    addingFile = new File(getGitFolder() + File.separator + relative);
                    addingFile.getParentFile().mkdirs();
                } else {
                    addingFile = source;
                }

                Files.copy(file.toPath(), addingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if (addToIndex) gitIndex.addFile(file, addingFile);
            }

            result.accept(addingFile, relative);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRelativeClass(File file) {
        File inputImage = ProjectManager.getPPFProject().getInputLocation();
        if (inputImage.isFile()) {
            new Exception("Tried to get relative class not in the input image path!").printStackTrace();
            return "";
        }

        return inputImage.getParentFile().toURI().relativize(file.toURI()).getPath().replace("/", File.separator);
    }

    private Pattern hideOriginPattern = Pattern.compile("add origin(.*)");

    public void setHideOrigin(boolean hideOrigin) {
        this.hideOrigin = hideOrigin;
        if (hideOrigin) {
            this.commandOutputModifiers.putIfAbsent("hideOrigin", string -> {
                Matcher matcher = hideOriginPattern.matcher(string);

                return matcher.find() ? matcher.replaceFirst("<hidden>") : string;
            });
        } else {
            this.commandOutputModifiers.remove("hideOrigin");
        }
    }

    public void setRemoteOrigin(String url) {
        if (url == null || url.isEmpty()) {
            LOGGER.error("No URL for remote origin found");
            this.mainGUI.setHaveError();
            return;
        }

        runCommand("git remote -v", true, getGitFolder(), result -> {
            Runnable addOrigin = () -> runCommand("git remote add origin " + url, true, false, getGitFolder(), lastResult -> {
                if (result.contains("remote origin already exists")) {
                    LOGGER.error("Couldn't add as a remote origin.");
                } else {
                    LOGGER.info("Added " + (this.hideOrigin ? "<hidden>" : url) + " as a remote origin");
                }
            });

            if (result.contains("origin")) {
                runCommand("git remote remove origin", true, getGitFolder(), ignored -> addOrigin.run());
            } else {
                addOrigin.run();
            }
        });
    }

    public void commit(String message) throws IOException {
        if (message == null || message.isEmpty()) {
            LOGGER.error("No commit message found");
            this.mainGUI.setHaveError();
            return;
        }

        GitIndex gitIndex = getGitIndex();
        gitIndex.getAdded().forEach((image, source) -> {
            File imageFile = new File(image);
            File sourceFile = new File(source);

            ModifiedDetector modifiedDetector = new ModifiedDetector(imageFile, sourceFile);

            if (modifiedDetector.imageChanged()) {
                moveOrScan(imageFile, sourceFile, false, null, (addingFile, relative) ->
                        LOGGER.info("Moved/scanned file " + relative));
            }
        });

        runCommand("git commit -a -m \"" + message + "\"", true, getGitFolder(), result -> {
            if (result.contains("changed")) {
                LOGGER.info("Successfully committed");
            } else if (result.contains("nothing added")) {
                LOGGER.info("Nothing changed; couldn't commit");
            } else {
                LOGGER.warn("Unexpected git command result: \n" + result + "\nIf this looks correct, please make an issue on the MS Paint IDE GitHub: https://github.com/RubbaBoy/MSPaintIDE/issues/new");
            }
        });
    }

    public void hasUnpushedCommits(Consumer<Boolean> result) {
        runCommand("git log origin/master..HEAD", true, getGitFolder(), logResult -> result.accept(logResult.contains("commit")));
    }

    public void push() {
        runCommand("git remote -v", true, getGitFolder(), result -> {
            if (!result.contains("origin")) {
                LOGGER.error("No origin set up, couldn't push");
                return;
            }

            runCommand("git push -u origin master", true, getGitFolder(), pushResult -> {
                if (pushResult.contains("does not appear to be a git repository")) {
                    LOGGER.error("No origin set up, couldn't push");
                    return;
                }

                if (pushResult.contains("unknown revision or path not in the working tree")) {
                    LOGGER.warn("The branch isn't set up properly, try re-adding your remote origin, making new commits, pushing again, or making an issue here: https://github.com/RubbaBoy/MSPaintIDE/issues/new");
                    LOGGER.warn("Full command response:");
                    LOGGER.warn(pushResult);
                    return;
                }

                hasUnpushedCommits(stillHasUnpushed -> {
                    if (stillHasUnpushed) {
                        this.mainGUI.setHaveError();
                        LOGGER.error("The push did not succeed, did your remote origin contain credentials? If not, please add them");
                    } else {
                        LOGGER.info("Pushed commits successfully");
                    }
                });
            });
        });
    }
}

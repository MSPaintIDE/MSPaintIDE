package com.uddernetworks.mspaint.project;

import com.google.gson.*;
import com.uddernetworks.mspaint.main.JFXWorkaround;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProjectManager {

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectManager.class);

    private static Path recent = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\recent").toPath();
    private static PPFProject ppfProject;
    private static Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(File.class, (JsonSerializer<File>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getAbsolutePath()))
            .registerTypeAdapter(File.class, (JsonDeserializer<File>) (srcElement, typeOfSrc, context) -> new File(srcElement.getAsString()))
            .create();
    private static List<PPFProject> recentProjects = new ArrayList<>();
    private static Consumer<PPFProject> projectConsumer;

    public static void closeCurrentProject() {
        if (ppfProject != null) save();
        ppfProject = null;
        writeRecent();
    }

    public static List<PPFProject> getRecent() {
        if (!recentProjects.isEmpty()) return recentProjects;

        try {
            AtomicBoolean open = new AtomicBoolean(false);
            recent.toFile().createNewFile();
            recentProjects = Files.readAllLines(recent).stream()
                    .filter(line -> {
                        if (line.equalsIgnoreCase("true") || line.equalsIgnoreCase("false")) {
                            open.set(line.equalsIgnoreCase("true"));
                            return false;
                        }
                        return true;
                    })
                    .map(File::new)
                    .filter(File::exists)
                    .map(file -> {
                        PPFProject project = null;

                        try {
                            project = gson.fromJson(new FileReader(file), PPFProject.class);
                            if (project != null) project.setFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return Optional.ofNullable(project);
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            if (open.get() && !recentProjects.isEmpty()) {
                ppfProject = recentProjects.get(0);
                projectConsumer.accept(ppfProject);
            }
            return recentProjects;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static void addRecent(PPFProject ppfProject) {
        recentProjects.remove(ppfProject);
        recentProjects.add(0, ppfProject);
    }

    public static void writeRecent() {
        try {
            recent.toFile().createNewFile();
            Files.write(recent, ((ppfProject == null ? "false" : "true") + "\n" + recentProjects.stream()
                    .map(PPFProject::getFile)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.joining("\n")))
                    .getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentProject(PPFProject ppfProject) {
        if (ProjectManager.ppfProject != null) save();
        ProjectManager.ppfProject = ppfProject;
    }

    public static PPFProject getPPFProject() {
        return ppfProject;
    }

    public static void save() {
        try {
            try (var writer = new FileWriter(ppfProject.getFile())) {
                IOUtils.write(gson.toJson(ppfProject), writer);
            }
        } catch (IOException e) {
            LOGGER.error("Exception saving the PPFProject", e);
        }
    }

    public static PPFProject readProject(File file) {
        try {
            return (ppfProject = gson.fromJson(new FileReader(file), PPFProject.class)).setFile(file);
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception reading the PPFProject", e);
            return ppfProject;
        }
    }

    private static PPFProject previous = null;

    public static void switchProject(PPFProject ppfProject) {
        setCurrentProject(ppfProject);
        save();
        addRecent(ppfProject);
        writeRecent();
        previous = ppfProject;

        if (previous != null) {
            try {
                LOGGER.info("Restarting self, this may take a few seconds...");
                StringBuilder cmd = new StringBuilder();
                cmd.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java ");
                for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                    cmd.append(jvmArg).append(" ");
                }
                cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
                cmd.append(JFXWorkaround.class.getName()).append(" ");
                cmd.append(ppfProject.getFile().getAbsolutePath());
                Runtime.getRuntime().exec(cmd.toString());
                System.exit(0);
                return;
            } catch (IOException e) {
                LOGGER.error("Error while trying to self-restart");
            }
        }

        projectConsumer.accept(ppfProject);
    }

    public static void switchProjectConsumer(Consumer<PPFProject> projectConsumer) {
        ProjectManager.projectConsumer = projectConsumer;
    }
}

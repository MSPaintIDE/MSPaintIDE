package com.uddernetworks.mspaint.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProjectManager {

    private static Path recent = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\recent").toPath();
    private static PPFProject ppfProject;
    private static PPFWriter ppfWriter = new PPFWriter();
    private static PPFReader ppfReader = new PPFReader();
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
                    .map(ppfReader::read)
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
        ppfWriter.write(ppfProject);
    }

    public static PPFProject readProject(File file) {
        System.out.println("Reading 222");
        return (ppfProject = ppfReader.read(file));
    }

    public static void switchProject(PPFProject ppfProject) {
        System.out.println("Switching project");
        setCurrentProject(ppfProject);
        save();
        addRecent(ppfProject);
        writeRecent();

        projectConsumer.accept(ppfProject);
    }

    public static void switchProjectConsumer(Consumer<PPFProject> projectConsumer) {
        ProjectManager.projectConsumer = projectConsumer;
    }
}

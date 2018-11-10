package com.uddernetworks.mspaint.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectManager {

    private static Path recent = new File(System.getProperties().getProperty("user.home"), "AppData\\Local\\MSPaintIDE\\recent").toPath();
    private static PPFProject ppfProject;
    private static PPFWriter ppfWriter = new PPFWriter();
    private static PPFReader ppfReader = new PPFReader();
    private static List<PPFProject> recentProjects = new ArrayList<>();

    public static List<PPFProject> getRecent() {
        if (!recentProjects.isEmpty()) return recentProjects;

        try {
            recent.toFile().createNewFile();
            return (recentProjects = Files.readAllLines(recent).stream()
                    .map(File::new)
                    .filter(File::exists)
                    .map(ppfReader::read)
                    .collect(Collectors.toList()));
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
            Files.write(recent, recentProjects.stream()
                    .map(PPFProject::getFile)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.joining("\n"))
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
        return (ppfProject = ppfReader.read(file));
    }
}

package com.uddernetworks.mspaint.project;

import java.io.File;

public class ProjectManager {

    private static PPFProject ppfProject;
    private static PPFWriter ppfWriter = new PPFWriter();
    private static PPFReader ppfReader = new PPFReader();

    public static void setCurrentProject(PPFProject ppfProject) {
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

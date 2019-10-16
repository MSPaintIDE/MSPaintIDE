package com.uddernetworks.mspaint.code.languages.java.buildsystem.gradle;

import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.gradle.tooling.model.Task;
import org.gradle.util.GradleVersion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GradleConnector {

    public static void main(String[] args) {
        var connector = new GradleConnector("E:\\MSPaintIDE", "E:\\MSPaintIDE");
        System.out.println("Using Gradle version: " + connector.getGradleVersion());
        System.out.println("Tasks:");
        connector.getGradleTasks().forEach(task -> {
            System.out.println(task.getName());
        });
    }

    private org.gradle.tooling.GradleConnector connector;

    public GradleConnector(String gradleInstallationDir, String projectDir) {
        var gradleInstallationDir1 = new File(gradleInstallationDir);
        connector = org.gradle.tooling.GradleConnector.newConnector();
//        connector.useInstallation(gradleInstallationDir1);
        connector.forProjectDirectory(new File(projectDir));
    }

    public String getGradleVersion() {
        return GradleVersion.current().getVersion();
    }

    public List<String> getGradleTaskNames() {
        List<String> taskNames = new ArrayList<>();
        List<GradleTask> tasks = getGradleTasks();
        return tasks.stream().map(Task::getName).collect(Collectors.toList());
    }

    public List<GradleTask> getGradleTasks() {
        List<GradleTask> tasks;
        try (ProjectConnection connection = connector.connect()) {
            GradleProject project = connection.getModel(GradleProject.class);
            tasks = new ArrayList<>(project.getTasks());
        }
        return tasks;
    }
}

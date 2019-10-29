package com.uddernetworks.mspaint.code.languages.java.buildsystem.gradle;

import com.uddernetworks.mspaint.logging.LogPipe;
import com.uddernetworks.mspaint.main.StartupLogic;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;
import org.gradle.tooling.model.Task;
import org.gradle.util.GradleVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GradleConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradleConnector.class);
    private final StartupLogic startupLogic;
    private final File projectDir;

    private org.gradle.tooling.GradleConnector connector;

    public GradleConnector(StartupLogic startupLogic, File projectDir) {
        this.startupLogic = startupLogic;
        this.projectDir = projectDir;
        LOGGER.info("Connector connected at {}", projectDir.getAbsolutePath());
        connector = org.gradle.tooling.GradleConnector.newConnector();
        connector.forProjectDirectory(projectDir);

        Runtime.getRuntime().addShutdownHook(new Thread(DefaultGradleConnector::close));
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

    public void runTask(String... tasks) {
        runTask(System.out, tasks);
    }

    public void runTask(LogPipe logPipe, String... tasks) {
        if (logPipe == null) {
            runTask(tasks);
        } else {
            runTask(logPipe.getStandardOut(), logPipe.getErrorOut(), tasks);
        }
    }

    public void runTask(OutputStream out, String... tasks) {
        runTask(out, System.err, tasks);
    }

    public void runTask(OutputStream out, OutputStream err, String... tasks) {
        LOGGER.info("Running task(s) {}", String.join(" ", tasks));
        try (ProjectConnection connection = connector.connect()) {
            connection.newBuild().setStandardOutput(out).setStandardError(err).forTasks(tasks).run(new ResultHandler<>() {
                @Override
                public void onComplete(Void result) {
                    LOGGER.info("Finished {}", String.join(" ", tasks));
                }

                @Override
                public void onFailure(GradleConnectionException failure) {
                    LOGGER.error("Failed to execute " + String.join(" ", tasks), failure.fillInStackTrace());
                }
            });
        }
    }
}

package com.uddernetworks.mspaint.logging;

import org.apache.log4j.spi.LoggingEvent;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ThreadedLogger {

    private static List<LogPipe> pipes = new ArrayList<>();

    public static void addPipe(OutputStream allOut, String name, Class<?>... classes) {
        addPipe(allOut, allOut, name, classes);
    }

    public static void addPipe(OutputStream standardOut, OutputStream errorOut, String name, Class<?>... classes) {
        pipes.add(new LogPipe(name, standardOut, errorOut, Arrays.asList(classes)));
    }

    public static void removePipe(String name) {
        getPipeFromName(name).ifPresent(pipes::remove);
    }

    public static Optional<LogPipe> getPipeFromName(String name) {
        return pipes.stream().filter(pipe -> pipe.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static Optional<LogPipe> getPipeFromClass(String name) {
        return pipes.stream().filter(pipe -> pipe.containsClass(name)).findFirst();
    }

    public static void pipeEvent(LoggingEvent event) {
        getPipeFromClass(event.getLogger().getName()).ifPresent(pipe -> pipe.consume(event));
    }

}

package com.uddernetworks.mspaint.logging;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class LogPipe {

    private String name;
    private OutputStream standardOut;
    private OutputStream errorOut;
    private List<Class<?>> classes;
    private List<String> classNames;

    public LogPipe(String name, OutputStream standardOut, OutputStream errorOut, List<Class<?>> classes) {
        this.name = name;
        this.standardOut = standardOut;
        this.errorOut = errorOut;
        this.classes = classes;
        this.classNames = classes.stream().map(Class::getName).collect(Collectors.toList());
    }

    public String getName() {
        return this.name;
    }

    public OutputStream getStandardOut() {
        return standardOut;
    }

    public OutputStream getErrorOut() {
        return errorOut;
    }

    public List<Class<?>> getClasses() {
        return this.classes;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public boolean containsClass(Class<?> clazz) {
        return this.classes.contains(clazz);
    }

    public boolean containsClass(String name) {
        return this.classNames.contains(name);
    }

    public void consume(LoggingEvent event) {
        try {
            IOUtils.write(format(event), this.standardOut);

            String[] stackTrace = event.getThrowableStrRep();
            if (stackTrace != null) {
                for (var value : stackTrace) {
                    IOUtils.write(value + Layout.LINE_SEP, this.errorOut);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Not printing to logger as it may cause a recursive call
        }
    }

    private static String format(LoggingEvent event) {
        return FormattedAppender.getInstance().getLayout().format(event);
    }
}

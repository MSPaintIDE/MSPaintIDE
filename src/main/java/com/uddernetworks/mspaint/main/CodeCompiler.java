package com.uddernetworks.mspaint.main;

import javax.tools.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

public class CodeCompiler {

    private File classOutputFolder;
    private List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();

    public class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            errors.add(diagnostic);
        }
    }

    public class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String contents;

        private InMemoryJavaFileObject(String className, String contents) {
            super(URI.create("string:///" + className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return contents;
        }
    }

    private JavaFileObject getJavaFileObject(String code, String classPackage, String className) {
        JavaFileObject so = null;
        try {
            so = new InMemoryJavaFileObject(classPackage + "." + className, code);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return so;
    }

    private void compile(Iterable<? extends JavaFileObject> files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);

        Iterable options = Arrays.asList("-d", classOutputFolder.getAbsolutePath());
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
        task.call();
    }

    private void runIt(String classPackage, String className) {
        try {
            URL url = classOutputFolder.toURL();
            URL[] urls = new URL[]{url};

            ClassLoader loader = new URLClassLoader(urls);

            Class thisClass = loader.loadClass(classPackage + "." + className);

            Object instance = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod("main", String[].class);

            thisMethod.invoke(instance, new Object[] { new String[0] });
        } catch (MalformedURLException | ClassNotFoundException ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Diagnostic<? extends JavaFileObject>> compileAndExecute(String text, File classOutputFolder, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream) {
        this.classOutputFolder = classOutputFolder;
        classOutputFolder.mkdirs();

        PrintStream imageOut = new PrintStream(imageOutputStream);
        PrintStream compilerOut = new PrintStream(compilerStream);
        compilerStream.changeColor(Color.RED);

        PrintStream oldPS = System.out;
        System.setOut(imageOut);

        long start = System.currentTimeMillis();
        compilerOut.println("Compiling...");

        String classPackage = (text.trim().startsWith("package") ? text.trim().substring(8, text.trim().indexOf(";")) : "");
        String[] spaces = text.trim().split(" ");
        String className = "Main";
        for (int i = 0; i < spaces.length; i++) {
            if (spaces[i].equals("class")) {
                className = spaces[i + 1];
                break;
            }
        }

        compilerOut.println("Class name = " + className);
        compilerOut.println("Class package = " + classPackage);

        JavaFileObject file = getJavaFileObject(text, classPackage, className);
        Iterable<? extends JavaFileObject> files = Arrays.asList(file);

        compile(files);

        compilerOut.println("Compiled in " + String.valueOf((System.currentTimeMillis() - start)) + "ms");
        compilerOut.println("Executing...");
        start = System.currentTimeMillis();

        runIt(classPackage, className);

        if (!errors.isEmpty()) {
            for (Diagnostic<? extends JavaFileObject> error : errors) {
                compilerOut.println("Error on " + error.getSource().getName() + " [" + error.getLineNumber() + ":" + (error.getColumnNumber() == -1 ? "?" : error.getColumnNumber()) + "] " + error.getMessage(Locale.ENGLISH));
            }
        }

        System.setOut(oldPS);
        System.out.println("errors = " + errors);

        compilerOut.println("Executed in " + (System.currentTimeMillis() - start) + "ms");

        return errors;
    }
}
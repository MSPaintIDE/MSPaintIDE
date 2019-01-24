package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.FileJarrer;
import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CodeCompiler {

    private static Logger LOGGER = LoggerFactory.getLogger(CodeCompiler.class);

    private File classOutputFolder;
    private Map<String, ImageClass> imageClassHashMap = new HashMap<>();
    private Map<ImageClass, List<Diagnostic<? extends JavaFileObject>>> errors = new HashMap<>();
    private List<URLClassLoader> classLoaders = new ArrayList<>();

    public class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            String packageName = diagnostic.getSource().getName().substring(1).replace("/", ".");
            packageName = packageName.substring(0, packageName.length() - 5);

            LOGGER.info("packageName = " + packageName);

            ImageClass imageClass = imageClassHashMap.get(packageName);

            if (errors.containsKey(imageClass)) {
                errors.get(imageClass).add(diagnostic);
            } else {
                List<Diagnostic<? extends JavaFileObject>> list = new ArrayList<>();
                list.add(diagnostic);
                errors.put(imageClass, list);
            }
        }
    }

    public static class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private String contents;

        private InMemoryJavaFileObject(String className, String contents) {
            super(URI.create("string:///" + className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }

        @Override
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

    private void compile(Iterable<? extends JavaFileObject> files, List<File> libs) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);

        List<String> options = new ArrayList<>(Arrays.asList("-d", classOutputFolder.getAbsolutePath()));

        if (!libs.isEmpty()) {
            options.add("-classpath");

            libs.forEach(lib -> options.add(lib.getAbsolutePath()));
        }

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
        task.call();
    }

    private void runIt(String classPackage, String className) {
        try {
            classLoaders.add(new URLClassLoader(new URL[]{classOutputFolder.toURI().toURL()}));

            Class<?> thisClass = classLoaders.get(classLoaders.size() - 1).loadClass(classPackage.trim().isEmpty() ? className : classPackage + "." + className);

            Object instance = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod("main", String[].class);

            thisMethod.invoke(instance, new Object[]{new String[0]});
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ignored) {}
    }

    public Map<ImageClass, List<Diagnostic<? extends JavaFileObject>>> compileAndExecute(List<ImageClass> imageClasses, File jarFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException {
        this.classOutputFolder = classOutputFolder;
        classOutputFolder.mkdirs();

        for (File file : getFilesFromDirectory(classOutputFolder, null)) {
            file.delete();
        }

        PrintStream imageOut = new PrintStream(imageOutputStream);
        PrintStream compilerOut = new PrintStream(compilerStream);
        compilerStream.changeColor(Color.RED);

        PrintStream oldPS = System.out;
        System.setOut(imageOut);

        long start = System.currentTimeMillis();
        compilerOut.println("Compiling...");

        mainGUI.setStatusText("Compiling...");

        List<JavaFileObject> filesList = new ArrayList<>();

        Map<String, String> namePackages = new HashMap<>();

        for (ImageClass imageClass : imageClasses) {
            String classPackage = (imageClass.getText().trim().startsWith("package") ? imageClass.getText().trim().substring(8, imageClass.getText().trim().indexOf(";")) : "");
            String[] spaces = imageClass.getText().trim().split(" ");
            String className = "Main";
            for (int i = 0; i < spaces.length; i++) {
                if (spaces[i].equals("class")) {
                    className = spaces[i + 1];
                    break;
                }
            }

            if (className.trim().endsWith("{")) className = className.trim().substring(0, className.trim().length() - 1);

            compilerOut.println("Class name = " + className);
            compilerOut.println("Class package = " + classPackage);

            namePackages.put(className, classPackage);
            imageClassHashMap.put(classPackage + "." + className, imageClass);

            filesList.add(getJavaFileObject(imageClass.getText(), classPackage, className));
        }

        compile(filesList, libs);

        compilerOut.println("Compiled in " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        compilerOut.println("Packaging jar...");
        mainGUI.setStatusText("Packaging jar...");

        if (otherFiles != null) {
            if (otherFiles.isDirectory()) {
                copyFolder(otherFiles, classOutputFolder);
            } else {
                File newLoc = new File(classOutputFolder, otherFiles.getName());
                newLoc.createNewFile();
                Files.copy(Paths.get(otherFiles.getAbsolutePath()), Paths.get(newLoc.getAbsolutePath()), REPLACE_EXISTING);
            }
        }

        FileJarrer fileJarrer = new FileJarrer(classOutputFolder, jarFile);
        fileJarrer.jarDirectory();

        LOGGER.info("Packaged jar in " + (System.currentTimeMillis() - start) + "ms");

        if (!errors.isEmpty()) {
            for (List<Diagnostic<? extends JavaFileObject>> errorList : errors.values()) {
                for (Diagnostic<? extends JavaFileObject> error : errorList) {
                    compilerOut.println("Error on " + error.getSource().getName() + " [" + error.getLineNumber() + ":" + (error.getColumnNumber() == -1 ? "?" : error.getColumnNumber()) + "] " + error.getMessage(Locale.ENGLISH));
                }
            }
        }

        if (!execute) {
            System.setOut(oldPS);
            return errors;
        }

        compilerOut.println("Executing...");
        mainGUI.setStatusText("Executing...");
        start = System.currentTimeMillis();

        for (String className : namePackages.keySet()) {
            runIt(namePackages.get(className), className);
        }

        System.setOut(oldPS);

        compilerOut.println("Executed in " + (System.currentTimeMillis() - start) + "ms");

        mainGUI.setStatusText("");

        for (URLClassLoader classLoader : this.classLoaders) {
            classLoader.close();
        }

        this.classLoaders.clear();

        return errors;
    }


    private static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {

            if (!dest.exists()) dest.mkdir();

            for (String file : src.list()) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }
        } else {
            try (InputStream in = new FileInputStream(src)) {
                try (OutputStream out = new FileOutputStream(dest)) {

                    byte[] buffer = new byte[1024];

                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            }
        }
    }


    private List<File> getFilesFromDirectory(File directory, String extension) {
        List<File> ret = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ret.add(file);
                ret.addAll(getFilesFromDirectory(file, extension));
            } else {
                if (extension == null) ret.add(file);
                else if (file.getName().endsWith("." + extension)) ret.add(file);
            }
        }

        return ret;
    }
}
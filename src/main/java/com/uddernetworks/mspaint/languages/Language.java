package com.uddernetworks.mspaint.languages;

import com.uddernetworks.mspaint.imagestreams.ImageOutputStream;
import com.uddernetworks.mspaint.main.ImageClass;
import com.uddernetworks.mspaint.main.MainGUI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Language {
    /**
     * Gets the name of the Language, e.g. "Java", "Python".
     * @return the name of the language
     */
    String getName();

    /**
     * Gets the file extensions to be used by files that will be parsed by the language. This does not include the period, examples include "java", "py".
     * @return the file extensions used
     */
    String[] getFileExtensions();

    /**
     * Gets the extension of the compiled/packaged output file that is generated from the source code. An example of an output of this method is "jar" for Java.
     * The method may return null if the language does not support output/packaged files.
     * @return the extension of the output file
     */
    String getOutputFileExtension();

    /**
     * Gets if the language is interpreted (Compared to being compiled).
     * @return if the language is interpreted
     */
    boolean isInterpreted();

    /**
     * Gets if the language has the correct software/libraries needed to compile/interpret and execute the language on the system.
     * @return if the system meets the requirements to use the language
     */
    boolean meetsRequirements();

    /**
     * Gets the language's LanguageHighlighter for custom highlighting
     * @return the language's LanguageHighlighter
     */
    LanguageHighlighter getLanguageHighlighter();

    /**
     * Compiles and/or executes the given image. If the language does not compile, it will interpret the files.
     * @param imageClasses The image files to be compiled and executed
     * @param outputFile The packaged output file, if required
     * @param otherFiles The directory or single file in which to put in the output file
     * @param classOutputFolder The folder to be used to compile individual files. E.g. a folder to hold all the .class files of a Java project
     * @param mainGUI The main instance of MainGUI
     * @param imageOutputStream The ImageOutputStream that is used for all executed program output
     * @param compilerStream The ImageOutputStream that is used for all compilation-related output
     * @param libs The containing folder or file to be used as libraries for the program
     * @param execute If the program should be executed along with compiling (Or interpreting) and packaging the jar
     * @throws IOException If an IO Exception occurs
     */
    Map<ImageClass, List<LanguageError>> compileAndExecute(List<ImageClass> imageClasses, File outputFile, File otherFiles, File classOutputFolder, MainGUI mainGUI, ImageOutputStream imageOutputStream, ImageOutputStream compilerStream, List<File> libs, boolean execute) throws IOException;
}

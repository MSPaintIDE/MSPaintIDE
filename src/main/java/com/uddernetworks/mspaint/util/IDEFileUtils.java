package com.uddernetworks.mspaint.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IDEFileUtils {

    public static List<File> getFilesFromDirectory(File directory, String extension) {
        return getFilesFromDirectory(directory, new String[] {extension});
    }

    public static List<File> getFilesFromDirectory(File directory, String[] extensions) {
        List<File> ret = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                ret.addAll(getFilesFromDirectory(file, extensions));
            } else {
                if (extensions == null || Arrays.stream(extensions).anyMatch(extension -> file.getName().endsWith("." + extension))) ret.add(file);
            }
        }

        return ret;
    }

    public static List<File> getFilesFromDirectory(File directory, String[] extensions, String postExtension) {
        return getFilesFromDirectory(directory, Arrays.stream(extensions).map(string -> string + "." + postExtension).toArray(String[]::new));
    }

}

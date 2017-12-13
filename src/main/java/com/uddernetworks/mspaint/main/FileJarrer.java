package com.uddernetworks.mspaint.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class FileJarrer {

    private List<String> fileList = new ArrayList<>();

    private File sourceFile;
    private File outFile;

    public FileJarrer(File sourceDir, File outFile) {
        this.sourceFile = sourceDir;
        this.outFile = outFile;
    }

    private void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            JarOutputStream jarOutputStream = new JarOutputStream(fos);

            for (String file : this.fileList) {
                JarEntry jarEntry = new JarEntry(file.replace("\\", "/"));
                jarOutputStream.putNextEntry(jarEntry);

                FileInputStream in = new FileInputStream(sourceFile.getAbsolutePath() + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    jarOutputStream.write(buffer, 0, len);
                }

                in.close();
            }

            jarOutputStream.closeEntry();
            jarOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void genFileList(File node) {
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                genFileList(new File(node, filename));
            }
        }

    }

    private String generateZipEntry(String file) {
        return file.substring(sourceFile.getAbsolutePath().length() + 1, file.length());
    }

    public void jarDirectory() {
        genFileList(sourceFile);
        zipIt(outFile.getAbsolutePath());
    }

}

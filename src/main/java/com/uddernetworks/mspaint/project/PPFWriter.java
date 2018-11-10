package com.uddernetworks.mspaint.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PPFWriter {

    private File file;

    public static void main(String[] args) {
        PPFProject ppfProject = new PPFProject();
        ppfProject.setInputLocation(new File("MSPaintIDE\\input"));
        ppfProject.setHighlightLocation(new File("MSPaintIDE\\highlight"));
        ppfProject.setClassLocation(new File("MSPaintIDE\\class"));
        ppfProject.setJarFile(new File("MSPaintIDE\\jar.jar"));
        ppfProject.setLibraryLocation(new File("MSPaintIDE\\library"));
        ppfProject.setOtherLocation(new File("MSPaintIDE\\other"));
        ppfProject.setCompilerOutput(new File("MSPaintIDE\\compiler.png"));
        ppfProject.setAppOutput(new File("MSPaintIDE\\app.png"));



        PPFWriter ppfWriter = new PPFWriter(new File("main.ppf"));
        ppfWriter.write(ppfProject);
    }

    public PPFWriter(File file) {
        this.file = file;
    }

    public void write(PPFProject ppfProject) {
        List<Byte> bytes = new ArrayList<>();
        Arrays.stream(ppfProject.getClass().getDeclaredFields()).forEach(field -> {
            BinaryIdentifier binaryIdentifier = BinaryIdentifier.fromField(field);
            if (binaryIdentifier == null) {
                System.err.println("No binary identifier for: " + field.getName());
                return;
            }

            bytes.add(binaryIdentifier.getBinary());

            for (byte b : binaryIdentifier.getValue(ppfProject)) {
                bytes.add(b);
            }

            bytes.add(BinaryIdentifier.END_VALUE);
        });

        try (FileOutputStream fileOuputStream = new FileOutputStream(file)) {
            fileOuputStream.write(ByteUtils.byteListToArray(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

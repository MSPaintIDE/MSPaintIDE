package com.uddernetworks.mspaint.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PPFReader {

    public static void main(String[] args) throws IllegalAccessException {
        PPFReader ppfReader = new PPFReader();

        PPFProject ppfProject = ppfReader.read(new File("mainn.ppf"));

        for (Field declaredField : ppfProject.getClass().getDeclaredFields()) {
            if (!declaredField.getName().equals("map")) continue;
            declaredField.setAccessible(true);
            System.out.println(declaredField.getName() + " = " + declaredField.get(ppfProject));
        }
    }

    public PPFProject read(File file) {
        PPFProject ppfProject = new PPFProject(file);
        try {
            byte[] bFile = new byte[(int) file.length()];

            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);

            boolean reading = false;
            BinaryIdentifier binaryIdentifier = null;
            List<Byte> byteList = new ArrayList<>();

            for (byte curr : bFile) {
                if (reading) {
                    if (curr == BinaryIdentifier.END_VALUE) {
                        if (byteList.size() > 0) {
                            binaryIdentifier.setValue(ByteUtils.byteListToArray(byteList), ppfProject);
                            byteList.clear();
                        }

                        reading = false;
                        continue;
                    }

                    byteList.add(curr);
                } else {
                    BinaryIdentifier potBinaryIdentifier = BinaryIdentifier.fromByte(curr);
                    if (potBinaryIdentifier != null) {
                        reading = true;
                        binaryIdentifier = potBinaryIdentifier;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't read project file correctly");
            e.printStackTrace();
        }

        return ppfProject;
    }

}

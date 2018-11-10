package com.uddernetworks.mspaint.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PPFReader {

    private File file;

    public static void main(String[] args) throws IOException, IllegalAccessException {
        PPFReader ppfReader = new PPFReader(new File("main.ppf"));

        PPFProject ppfProject = ppfReader.read();

        for (Field declaredField : ppfProject.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            System.out.println(declaredField.getName() + " = " + declaredField.get(ppfProject));
        }
    }

    public PPFReader(File file) {
        this.file = file;
    }

    public PPFProject read() throws IOException {
        PPFProject ppfProject = new PPFProject();
        byte[] bFile = new byte[(int) file.length()];

        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);

        boolean reading = false;
        BinaryIdentifier binaryIdentifier = null;
        List<Byte> byteList = new ArrayList<>();

        for (byte curr : bFile) {
            if (reading) {
                if (curr == BinaryIdentifier.END_VALUE) {
                    binaryIdentifier.setValue(ByteUtils.byteListToArray(byteList), ppfProject);
                    byteList.clear();
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

        return ppfProject;
    }

}

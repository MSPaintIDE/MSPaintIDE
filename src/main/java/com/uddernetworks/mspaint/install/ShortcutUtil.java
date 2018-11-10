package com.uddernetworks.mspaint.install;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ShortcutUtil {
    public static void makeAdmin(File createFor) throws IOException {
        System.out.println(createFor.exists());
        byte[] bytes = Files.readAllBytes(createFor.toPath());
        byte b = bytes[21];
        b = (byte) (b | (1 << 5));
        bytes[21] = b;
        Files.write(createFor.toPath(), bytes, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

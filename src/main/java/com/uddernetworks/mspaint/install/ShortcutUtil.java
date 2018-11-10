package com.uddernetworks.mspaint.install;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ShortcutUtil {
    public static void makeAdmin(File createFor) throws IOException {
        byte[] bytes = Files.readAllBytes(createFor.toPath());
        bytes[21] = (byte) (bytes[21] | (1 << 5));
        Files.write(createFor.toPath(), bytes, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

package com.uddernetworks.mspaint.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExtractUtils {

    /*
     * Copyright (c) 2012-2018 Red Hat, Inc.
     * All rights reserved. This program and the accompanying materials
     * are made available under the terms of the Eclipse Public License v1.0
     * which accompanies this distribution, and is available at
     * http://www.eclipse.org/legal/epl-v10.html
     *
     * Contributors:
     *   Red Hat, Inc. - initial API and implementation
     */
    public static void untar(TarArchiveInputStream tarIn, File targetDir) throws IOException {
        byte[] b = new byte[4096];
        TarArchiveEntry tarEntry;
        while ((tarEntry = tarIn.getNextTarEntry()) != null) {
            final File file = new File(targetDir, tarEntry.getName());
            if (tarEntry.isDirectory()) {
                if (!file.mkdirs()) {
                    throw new IOException("Unable to create folder " + file.getAbsolutePath());
                }
            } else {
                final File parent = file.getParentFile();
                if (!parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new IOException("Unable to create folder " + parent.getAbsolutePath());
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    int r;
                    while ((r = tarIn.read(b)) != -1) {
                        fos.write(b, 0, r);
                    }
                }
            }
        }
    }

}

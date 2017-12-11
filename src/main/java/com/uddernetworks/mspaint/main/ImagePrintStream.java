package com.uddernetworks.mspaint.main;

import java.io.OutputStream;
import java.io.PrintStream;

public class ImagePrintStream extends PrintStream {

    public ImagePrintStream(OutputStream out) {
        super(out);
    }
}

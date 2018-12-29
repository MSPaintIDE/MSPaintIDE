package com.uddernetworks.mspaint.main;

import java.io.OutputStream;
import java.io.PrintStream;

public class PrintStreamStringCopy extends OutputStream {
    private StringBuilder string = new StringBuilder();
    private PrintStream original = System.out;

    public PrintStreamStringCopy() {
        System.setOut(new PrintStream(this));
    }

    @Override
    public void write(int b) {
        this.string.append((char) b);
        this.original.write(b);
    }

    public String getPrevious() {
        System.setOut(this.original);
        return this.string.toString();
    }
}

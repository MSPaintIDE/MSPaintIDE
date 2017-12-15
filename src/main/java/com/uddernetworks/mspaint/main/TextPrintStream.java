package com.uddernetworks.mspaint.main;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class TextPrintStream extends OutputStream {

    private JTextPane textArea;
    private PrintStream original;
    private String last = "";
    private StringBuilder builder = new StringBuilder();

    public TextPrintStream(JTextPane textArea, PrintStream original) {
        this.textArea = textArea;
        this.original = original;
    }


    @Override
    public void write(int b) {
        builder.append((char) b);
        original.write(b);
    }

    public void updateText() {
        if (!last.equals(builder.toString())) {
            last = builder.toString();
            textArea.setText(last);
        }
    }
}

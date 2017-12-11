package com.uddernetworks.mspaint.main;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;

public class TextPrintStream extends OutputStream {

    private JTextPane textArea;
    private StringBuilder builder = new StringBuilder();

    public TextPrintStream(JTextPane textArea) {
        this.textArea = textArea;
    }


    @Override
    public void write(int b) {
        builder.append((char) b);

        textArea.setText(builder.toString());
    }
}

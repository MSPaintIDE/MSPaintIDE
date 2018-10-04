package com.uddernetworks.mspaint.imagestreams;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;

public class TextPrintStream extends OutputStream {

    private TextArea textArea;
    private PrintStream original;
    private String last = ""; //TODO not used - probably bug?
    private StringBuilder builder = new StringBuilder(); //TODO not used - probably bug?

    public TextPrintStream(TextArea textArea, PrintStream original) {
        this.textArea = textArea;
        this.original = original;
    }


    @Override
    public void write(int b) {
        builder.append((char) b);
        original.write(b);
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }
}

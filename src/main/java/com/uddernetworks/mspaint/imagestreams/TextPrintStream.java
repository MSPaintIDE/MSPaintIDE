package com.uddernetworks.mspaint.imagestreams;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;

public class TextPrintStream extends OutputStream {

    private TextArea textArea;
    private PrintStream original;
    private boolean printOriginal;

    public TextPrintStream(TextArea textArea, PrintStream original) {
        this.textArea = textArea;
        this.original = original;
    }

    @Override
    public void write(int b) {
        if (this.printOriginal) original.write(b);
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }

    public boolean shouldPrintOriginal() {
        return printOriginal;
    }

    public void setPrintOriginal(boolean printOriginal) {
        this.printOriginal = printOriginal;
    }
}

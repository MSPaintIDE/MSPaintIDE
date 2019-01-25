package com.uddernetworks.mspaint.logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;

import java.io.IOException;
import java.io.OutputStream;

public class GUIConsoleAppender extends ConsoleAppender {

    private static TextArea textArea;
    private static StringBuilder previous = new StringBuilder();
    private static boolean activated;
    private static boolean copyingPrevious;
    private static SystemOutStream out;
    private static SystemErrStream err;

    public GUIConsoleAppender() {
    }

    public GUIConsoleAppender(Layout layout) {
        super(layout);
    }

    public GUIConsoleAppender(Layout layout, String target) {
        super(layout, target);
    }

    public static void activate(TextArea textArea) {
        GUIConsoleAppender.textArea = textArea;
        activated = true;
        copyingPrevious = true;
        out.writeString(previous.toString());
        previous.setLength(0);
        copyingPrevious = false;
    }

    public static void deactivate() {
        activated = false;
    }

    @Override
    public void activateOptions() {
        if (this.target.equals("System.err")) {
            this.setWriter(this.createWriter(err = new SystemErrStream()));
        } else {
            this.setWriter(this.createWriter(out = new SystemOutStream()));
        }
    }

    private class SystemOutStream extends OutputStream {
        public SystemOutStream() {
        }

        public void writeString(String string) {
            for (char cha : string.toCharArray()) {
                write((int) cha);
            }
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
            System.out.flush();
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
        }

        @Override
        public void write(int b) {
            if (!copyingPrevious) System.out.write(b);

            if (activated) {
                Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
            } else {
                previous.append((char) b);
            }
        }
    }

    private class SystemErrStream extends OutputStream {
        public SystemErrStream() {
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
            System.out.flush();
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
        }

        @Override
        public void write(int b) {
            if (!copyingPrevious) System.err.write(b);

            if (activated) {
                Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
            } else {
                previous.append((char) b);
            }
        }
    }

}

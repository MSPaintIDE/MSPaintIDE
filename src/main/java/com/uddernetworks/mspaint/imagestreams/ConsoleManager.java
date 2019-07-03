package com.uddernetworks.mspaint.imagestreams;

import java.io.PrintStream;

public class ConsoleManager {

    private static ThreadPrintStream outStream = new ThreadPrintStream(System.out);
    private static ThreadPrintStream errStream = new ThreadPrintStream(System.err);

    static {
        System.setOut(outStream);
        System.setErr(errStream);
    }

    public static void setOut(PrintStream out) {
        outStream.setThreadStream(out);
    }

    public static void setErr(PrintStream err) {
        errStream.setThreadStream(err);
    }

    public static void setAll(PrintStream all) {
        setOut(all);
        setErr(all);
    }

}

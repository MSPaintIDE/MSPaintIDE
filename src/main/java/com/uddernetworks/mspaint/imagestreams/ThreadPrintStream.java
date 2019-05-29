package com.uddernetworks.mspaint.imagestreams;

import java.io.PrintStream;
import java.util.Locale;

public class ThreadPrintStream extends PrintStream {

    private ThreadLocal<PrintStream> localPrintStream = new ThreadLocal<>();

    public ThreadPrintStream(PrintStream original) {
        super(original);
    }

    public void setThreadStream(PrintStream printStream) {
        this.localPrintStream.set(printStream);
    }

    // The following are just overridden print methods that instead of doing super.foo() does
    // this.localPrintStream.get().foo() to get the thread's PrintStream.

    @Override
    public void print(boolean b) {
        this.localPrintStream.get().print(b);
    }

    @Override
    public void print(char c) {
        this.localPrintStream.get().print(c);
    }

    @Override
    public void print(int i) {
        this.localPrintStream.get().print(i);
    }

    @Override
    public void print(long l) {
        this.localPrintStream.get().print(l);
    }

    @Override
    public void print(float f) {
        this.localPrintStream.get().print(f);
    }

    @Override
    public void print(double d) {
        this.localPrintStream.get().print(d);
    }

    @Override
    public void print(char[] s) {
        this.localPrintStream.get().print(s);
    }

    @Override
    public void print(String s) {
        this.localPrintStream.get().print(s);
    }

    @Override
    public void print(Object obj) {
        this.localPrintStream.get().print(obj);
    }

    @Override
    public void println() {
        this.localPrintStream.get().println();
    }

    @Override
    public void println(boolean x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(char x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(int x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(long x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(float x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(double x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(char[] x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(String x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public void println(Object x) {
        this.localPrintStream.get().println(x);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        return this.localPrintStream.get().printf(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        return this.localPrintStream.get().printf(l, format, args);
    }
}
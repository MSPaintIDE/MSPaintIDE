package com.uddernetworks.mspaint.code.execution;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}

package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.RunningCode;

public class JavaRunningCode extends RunningCode {

    public JavaRunningCode(Runnable runnable) {
        super(runnable);
    }

    @Override
    public void runCode() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stopExecution() {

    }
}

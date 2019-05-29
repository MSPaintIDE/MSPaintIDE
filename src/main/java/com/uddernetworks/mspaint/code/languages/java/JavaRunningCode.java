package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.execution.RunningCode;
import com.uddernetworks.mspaint.code.execution.ThrowableRunnable;

import java.util.Optional;

public class JavaRunningCode extends RunningCode {

    public JavaRunningCode(ThrowableRunnable runnable) {
        super(runnable);
    }

    @Override
    public void runCode() {
        setExitCode(0);
        String message = null;

        try {
            this.runnable.run();
            this.success.forEach(exitCode -> exitCode.accept(getExitCode()));
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getLocalizedMessage();
            this.error.forEach(consumer -> consumer.accept(e.getLocalizedMessage()));
        }

        var optionalUsing = Optional.ofNullable(message);
        this.any.forEach(consumer -> consumer.accept(getExitCode(), optionalUsing));
    }
}

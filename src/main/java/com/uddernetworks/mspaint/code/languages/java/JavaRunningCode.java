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
        String message = null;

        try {
            this.runnable.run();
            this.success.forEach(Runnable::run);
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getLocalizedMessage();
            this.error.forEach(consumer -> consumer.accept(e.getLocalizedMessage()));
        }

        var optionalUsing = Optional.ofNullable(message);
        this.any.forEach(consumer -> consumer.accept(optionalUsing));
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stopExecution() {

    }
}

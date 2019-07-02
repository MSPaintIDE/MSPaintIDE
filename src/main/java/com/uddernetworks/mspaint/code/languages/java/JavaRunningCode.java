package com.uddernetworks.mspaint.code.languages.java;

import com.uddernetworks.mspaint.code.execution.RunningCode;
import com.uddernetworks.mspaint.code.execution.ThrowableRunnable;
import com.uddernetworks.mspaint.code.execution.ThrowableSupplier;

import java.util.Optional;

public class JavaRunningCode extends RunningCode {

    public JavaRunningCode(ThrowableRunnable runnable) {
        super(runnable);
    }

    public JavaRunningCode(ThrowableSupplier<Integer> runnable) {
        super(runnable);
    }

    @Override
    public void runCode() {
        setExitCode(0);
        Exception error = null;

        try {
            if (this.runnable != null) {
                this.runnable.run();
            } else {
                setExitCode(this.supplier.get());
            }
        } catch (Exception e) {
            error = e;
        }

        if (error != null) {
            error.printStackTrace();
            Exception finalError = error;
            this.error.forEach(consumer -> consumer.accept(finalError.getLocalizedMessage()));
        } else {
            this.success.forEach(exitCode -> exitCode.accept(getExitCode()));
        }

        var optionalUsing = Optional.ofNullable(error == null ? null : error.getLocalizedMessage());
        this.any.forEach(consumer -> consumer.accept(getExitCode(), optionalUsing));
    }
}

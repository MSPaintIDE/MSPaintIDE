package com.uddernetworks.mspaint.code.execution;

import java.util.Optional;

public class DefaultRunningCode extends RunningCode {

    public DefaultRunningCode(ThrowableRunnable runnable) {
        super(runnable);
    }

    public DefaultRunningCode(ThrowableSupplier<Integer> runnable) {
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

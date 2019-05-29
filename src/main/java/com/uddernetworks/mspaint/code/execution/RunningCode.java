package com.uddernetworks.mspaint.code.execution;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class RunningCode {

    protected ThrowableRunnable runnable;

    protected List<Runnable> success = new LinkedList<>();
    protected List<Consumer<String>> error = new LinkedList<>();
    protected List<Consumer<Optional<String>>> any = new LinkedList<>();

    /**
     * @param runnable The task that actually runs the code
     */
    public RunningCode(ThrowableRunnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Runs the given task async to run the code.
     */
    public abstract void runCode();

    /**
     * If code is currently being ran from this object.
     *
     * @return If code is being ran
     */
    public abstract boolean isRunning();

    /**
     * Stops the execution of the code ran by, if it's still running.
     */
    public abstract void stopExecution();

    /**
     * Executed after the code was successfully executed.
     *
     * @param runnable The code to run
     * @return The current {@link RunningCode}
     */
    public RunningCode afterSuccess(Runnable runnable) {
        this.success.add(runnable);
        return this;
    }

    /**
     * Executed after the code executed with errors OR forcibly via {@link RunningCode#stopExecution()}.
     *
     * @param message The code to run with the stop reason
     * @return The current {@link RunningCode}
     */
    public RunningCode afterError(Consumer<String> message) {
        this.error.add(message);
        return this;
    }

    /**
     * Executed after the code executed for any reason. If it stopped from errors or forcibly via
     * {@link RunningCode#stopExecution()}, a message will be present.
     *
     * @param optionalMessage The code to run with the stop reason, if applicable
     * @return The current {@link RunningCode}
     */
    public RunningCode afterAll(Consumer<Optional<String>> optionalMessage) {
        this.any.add(optionalMessage);
        return this;
    }
}

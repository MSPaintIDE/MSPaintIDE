package com.uddernetworks.mspaint.code.execution;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class RunningCode {

    protected ThrowableRunnable runnable;
    protected Future<?> future;
    protected int exitCode = 0;

    protected List<Consumer<Integer>> success = new LinkedList<>();
    protected List<Consumer<String>> error = new LinkedList<>();
    protected List<BiConsumer<Integer, Optional<String>>> any = new LinkedList<>();

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
    public boolean isRunning() {
        return this.future != null && !this.future.isDone();
    }

    /**
     * Stops the execution of the code ran by, if it's still running.
     */
    public void stopExecution() {
        if (this.future != null) {
            this.exitCode = -1;
            this.future.cancel(true);
        }
    }

    /**
     * Sets the running future, used by the {@link RunningCodeManager}.
     *
     * @param future The future of the running code.
     */
    public void setRunningFuture(Future<?> future) {
        this.future = future;
    }

    /**
     * Gets the exit code of the program.
     *
     * @return The exit code
     */
    public int getExitCode() {
        return this.exitCode;
    }

    /**
     * Sets the exit code of the program.
     *
     * @param exitCode The exit code
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Executed after the code was successfully executed.
     *
     * @param exitCode The code to run, with the exit code
     * @return The current {@link RunningCode}
     */
    public RunningCode afterSuccess(Consumer<Integer> exitCode) {
        this.success.add(exitCode);
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
     * @param exitCodeMessage The code to run with the exit code and stop reason, if applicable
     * @return The current {@link RunningCode}
     */
    public RunningCode afterAll(BiConsumer<Integer, Optional<String>> exitCodeMessage) {
        this.any.add(exitCodeMessage);
        return this;
    }
}

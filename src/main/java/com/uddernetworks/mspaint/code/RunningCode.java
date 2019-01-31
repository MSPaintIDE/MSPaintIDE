package com.uddernetworks.mspaint.code;

public abstract class RunningCode {

    Runnable runnable;

    /**
     * @param runnable The task that actually runs the code
     */
    public RunningCode(Runnable runnable) {
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
}

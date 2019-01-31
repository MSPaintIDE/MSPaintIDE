package com.uddernetworks.mspaint.code;

public interface RunningCodeManager {

    /**
     * Runs the given {@link RunningCode} object.
     *
     * @param runningCode The {@link RunningCode} object to run
     */
    void runCode(RunningCode runningCode);

    /**
     * Stops any code running from the IDE
     */
    void stopRunning();
}

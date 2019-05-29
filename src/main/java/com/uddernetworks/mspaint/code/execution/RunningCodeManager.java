package com.uddernetworks.mspaint.code.execution;

import java.util.Optional;

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

    /**
     * Gets the actively running {@link RunningCode} if existent
     *
     * @return The current {@link RunningCode}
     */
    Optional<RunningCode> getRunningCode();
}

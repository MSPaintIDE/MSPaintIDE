package com.uddernetworks.mspaint.code.execution;

import javafx.beans.property.StringProperty;

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
     * If there is a current existent {@link RunningCode} and code is currently being ran from it.
     *
     * @return If code is being ran
     */
    boolean isRunning();

    /**
     * Gets the actively running {@link RunningCode} if existent
     *
     * @return The current {@link RunningCode}
     */
    Optional<RunningCode> getRunningCode();

    /**
     * Binds start/stop button text.
     *
     * @param startStopText The {@link StringProperty} from the {@link com.jfoenix.controls.JFXButton}
     */
    void bindStartButton(StringProperty startStopText);
}

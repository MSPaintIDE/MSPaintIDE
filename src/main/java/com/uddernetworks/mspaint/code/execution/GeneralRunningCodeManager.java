package com.uddernetworks.mspaint.code.execution;

import com.uddernetworks.mspaint.main.StartupLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GeneralRunningCodeManager implements RunningCodeManager {

    private static Logger LOGGER = LoggerFactory.getLogger(GeneralRunningCodeManager.class);

    private final AtomicReference<RunningCode> currentlyRunning = new AtomicReference<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private StartupLogic startupLogic;

    public GeneralRunningCodeManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    @Override
    public void runCode(RunningCode runningCode) {
        var currentRunning = this.currentlyRunning.get();
        if (currentRunning != null && currentRunning.isRunning()) {
            LOGGER.info("Code already running, so terminating its execution.");
            this.currentlyRunning.get().stopExecution();
        }

        this.currentlyRunning.set(runningCode);
        this.executor.execute(runningCode::runCode);

    }

    @Override
    public void stopRunning() {
        if (this.currentlyRunning.get() == null) return;
        this.currentlyRunning.get().stopExecution();
    }

    @Override
    public Optional<RunningCode> getRunningCode() {
        return Optional.ofNullable(currentlyRunning.get());
    }
}

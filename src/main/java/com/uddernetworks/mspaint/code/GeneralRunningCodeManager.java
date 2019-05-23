package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.main.StartupLogic;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GeneralRunningCodeManager implements RunningCodeManager {

    private final AtomicReference<RunningCode> currentlyRunning = new AtomicReference<>();
    private Executor executor = Executors.newCachedThreadPool();
    private StartupLogic startupLogic;

    public GeneralRunningCodeManager(StartupLogic startupLogic) {
        this.startupLogic = startupLogic;
    }

    @Override
    public void runCode(RunningCode runningCode) {
        if (this.currentlyRunning.get() != null) {
            if (this.currentlyRunning.get().isRunning()) this.currentlyRunning.get().stopExecution();
        }

        this.currentlyRunning.set(runningCode);
        executor.execute(runningCode::runCode);
    }

    @Override
    public void stopRunning() {
        if (this.currentlyRunning.get() == null) return;
        this.currentlyRunning.get().stopExecution();
    }

}

package com.uddernetworks.mspaint.code;

import com.uddernetworks.mspaint.main.Main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GeneralRunningCodeManager implements RunningCodeManager {

    private final AtomicReference<RunningCode> currentlyRunning = new AtomicReference<>();
    private Executor executor = Executors.newCachedThreadPool();
    private Main main;

    public GeneralRunningCodeManager(Main main) {
        this.main = main;
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

package com.uddernetworks.mspaint.code.execution;

public interface CompilationResult {

    Status getCompletionStatus();

    enum Status {
        COMPILE_COMPLETE, RUNNING
    }

}

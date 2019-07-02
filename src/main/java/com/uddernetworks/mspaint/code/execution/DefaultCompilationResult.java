package com.uddernetworks.mspaint.code.execution;

public class DefaultCompilationResult implements CompilationResult {

    private Status status;

    public DefaultCompilationResult(Status status) {
        this.status = status;
    }

    @Override
    public Status getCompletionStatus() {
        return this.status;
    }
}

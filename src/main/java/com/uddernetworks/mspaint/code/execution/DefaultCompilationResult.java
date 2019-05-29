package com.uddernetworks.mspaint.code.execution;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.LanguageError;

import java.util.List;
import java.util.Map;

public class DefaultCompilationResult implements CompilationResult {

    private Map<ImageClass, List<LanguageError>> errors;
    private Status status;

    public DefaultCompilationResult(Map<ImageClass, List<LanguageError>> errors, Status status) {
        this.errors = errors;
        this.status = status;
    }

    @Override
    public Map<ImageClass, List<LanguageError>> getErrors() {
        return this.errors;
    }

    @Override
    public Status getCompletionStatus() {
        return this.status;
    }
}

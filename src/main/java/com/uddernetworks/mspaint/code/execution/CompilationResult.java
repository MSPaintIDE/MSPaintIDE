package com.uddernetworks.mspaint.code.execution;

import com.uddernetworks.mspaint.code.ImageClass;
import com.uddernetworks.mspaint.code.languages.LanguageError;

import java.util.List;
import java.util.Map;

public interface CompilationResult {

    Map<ImageClass, List<LanguageError>> getErrors();

    Status getCompletionStatus();

    enum Status {
        COMPILE_COMPLETE, RUNNING
    }

}

package com.uddernetworks.mspaint.languages.python;

import com.uddernetworks.mspaint.languages.LanguageError;

public class PythonError implements LanguageError {

    private int lineNumber;
    private int columnNumber;
    private String source;
    private String message;

    public PythonError(int lineNumber, int columnNumber, String source, String message) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.source = source;
        this.message = message;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return this.columnNumber;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
